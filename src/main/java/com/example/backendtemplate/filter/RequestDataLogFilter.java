package com.example.backendtemplate.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.io.TeeOutputStream;
import org.json.JSONObject;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
@Order(1)
@RequiredArgsConstructor
public class RequestDataLogFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(final @NotNull HttpServletRequest httpServletRequest, final @NotNull HttpServletResponse httpServletResponse, final FilterChain chain) {
        Instant start = Instant.now();
        final String requestId = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        MDC.put("req-id", requestId);
        try {
            boolean allowSkippingRequest = skipRequestLogging(httpServletRequest);
            if (allowSkippingRequest) {
                chain.doFilter(httpServletRequest, httpServletResponse);
                return;
            }

            BufferedRequestWrapper bufferedRequest = new BufferedRequestWrapper(httpServletRequest);
            BufferedResponseWrapper bufferedResponse = new BufferedResponseWrapper(httpServletResponse);

            HashMap<String, Object> requestLogMessage = new HashMap<>();
            requestLogMessage.put("HTTP METHOD", httpServletRequest.getMethod());
            requestLogMessage.put("PATH", httpServletRequest.getServletPath());
            requestLogMessage.put("REMOTE ADDRESS", httpServletRequest.getRemoteAddr());
            try {
                JSONObject requestDataObject = new JSONObject(bufferedRequest.getRequestBody());
                requestLogMessage.put("REQUEST BODY", requestDataObject.toMap());
            } catch (Exception e) {
                if (!httpServletRequest.getMethod().equals("GET")) {
                    log.info("REQUEST BODY: Not a valid JSON string");
                }
            }


            log.info("Request: " + new ObjectMapper().writeValueAsString(requestLogMessage));

            chain.doFilter(bufferedRequest, bufferedResponse);

            JSONObject responseObject = new JSONObject(bufferedResponse.getContent());
            //skip logging data object
            boolean allowSkippingResponse = skipResponseLogging(httpServletRequest);
            if (allowSkippingResponse) {
                responseObject.remove("data");
            }
            if (bufferedResponse.getContentType().equals("application/json")) {
                log.info("Response: " + new ObjectMapper().writeValueAsString(responseObject.toMap()));
            }

        } catch (Exception e) {
            //Ignore
            e.printStackTrace();
        } finally {
            Instant finish = Instant.now();
            long time = Duration.between(start, finish).toMillis();
            log.info("Time spend to respond: " + time + " ms");
            MDC.remove("req-id");
        }
    }

    private boolean skipRequestLogging(HttpServletRequest httpServletRequest) {
//        return true;
        //Add request path if you want to bypass writing the request body to log
        String[] regs = {
                "/resource/(.*?)",
        };
        Matcher matcher;
        for (String pathExpr : regs) {
            matcher = Pattern.compile(pathExpr).matcher(httpServletRequest.getServletPath());
            if (matcher.find()) {
                log.info("Request: PATH: " + httpServletRequest.getServletPath());
                return true;
            }
        }
        return false;
    }

    private boolean skipResponseLogging(HttpServletRequest httpServletRequest) {
        //Add request path if you want to bypass writing the response body to log
        String[] regs = {
                "/config/termsAndConditions",
                "/api/bank/all/ceft",
        };
        Matcher matcher;
        for (String pathExpr : regs) {
            matcher = Pattern.compile(pathExpr).matcher(httpServletRequest.getServletPath());
            if (matcher.find()) {
                return true;
            }
        }
        return false;
    }

    private static final class BufferedRequestWrapper extends HttpServletRequestWrapper {

        private final ByteArrayOutputStream baos;
        private final byte[] buffer;
        private ByteArrayInputStream bais = null;
        private BufferedServletInputStream bsis = null;

        public BufferedRequestWrapper(HttpServletRequest req) throws IOException {
            super(req);
            // Read InputStream and store its content in a buffer.
            InputStream is = req.getInputStream();
            this.baos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int read;
            while ((read = is.read(buf)) > 0) {
                this.baos.write(buf, 0, read);
            }
            this.buffer = this.baos.toByteArray();
        }

        @Override
        public ServletInputStream getInputStream() {
            this.bais = new ByteArrayInputStream(this.buffer);
            this.bsis = new BufferedServletInputStream(this.bais);
            return this.bsis;
        }

        String getRequestBody() throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.getInputStream()));
            String line = null;
            StringBuilder inputBuffer = new StringBuilder();
            do {
                line = reader.readLine();
                if (null != line) {
                    inputBuffer.append(line.trim());
                }
            } while (line != null);
            reader.close();
            return inputBuffer.toString().trim();
        }
    }

    private static final class BufferedServletInputStream extends ServletInputStream {

        private final ByteArrayInputStream bais;

        public BufferedServletInputStream(ByteArrayInputStream bais) {
            this.bais = bais;
        }

        @Override
        public int available() {
            return this.bais.available();
        }

        @Override
        public int read() {
            return this.bais.read();
        }

        @Override
        public int read(byte[] buf, int off, int len) {
            return this.bais.read(buf, off, len);
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {

        }
    }

    public static class TeeServletOutputStream extends ServletOutputStream {

        private final TeeOutputStream targetStream;

        public TeeServletOutputStream(OutputStream one, OutputStream two) {
            targetStream = new TeeOutputStream(one, two);
        }

        @Override
        public void write(int arg0) throws IOException {
            this.targetStream.write(arg0);
        }

        public void flush() throws IOException {
            super.flush();
            this.targetStream.flush();
        }

        public void close() throws IOException {
            super.close();
            this.targetStream.close();
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {

        }
    }

    public static class BufferedResponseWrapper implements HttpServletResponse {

        HttpServletResponse original;
        TeeServletOutputStream tee;
        ByteArrayOutputStream bos;

        public BufferedResponseWrapper(HttpServletResponse response) {
            original = response;
        }

        public String getContent() {
            return bos.toString();
        }

        public PrintWriter getWriter() throws IOException {
            return original.getWriter();
        }

        public ServletOutputStream getOutputStream() throws IOException {
            if (tee == null) {
                bos = new ByteArrayOutputStream();
                tee = new TeeServletOutputStream(original.getOutputStream(), bos);
            }
            return tee;
        }

        @Override
        public String getCharacterEncoding() {
            return original.getCharacterEncoding();
        }

        @Override
        public void setCharacterEncoding(String charset) {
            original.setCharacterEncoding(charset);
        }

        @Override
        public String getContentType() {
            return original.getContentType();
        }

        @Override
        public void setContentType(String type) {
            original.setContentType(type);
        }

        @Override
        public void setContentLength(int len) {
            original.setContentLength(len);
        }

        @Override
        public void setContentLengthLong(long l) {
            original.setContentLengthLong(l);
        }

        @Override
        public int getBufferSize() {
            return original.getBufferSize();
        }

        @Override
        public void setBufferSize(int size) {
            original.setBufferSize(size);
        }

        @Override
        public void flushBuffer() throws IOException {
            tee.flush();
        }

        @Override
        public void resetBuffer() {
            original.resetBuffer();
        }

        @Override
        public boolean isCommitted() {
            return original.isCommitted();
        }

        @Override
        public void reset() {
            original.reset();
        }

        @Override
        public Locale getLocale() {
            return original.getLocale();
        }

        @Override
        public void setLocale(Locale loc) {
            original.setLocale(loc);
        }

        @Override
        public void addCookie(Cookie cookie) {
            original.addCookie(cookie);
        }


        @Override
        public boolean containsHeader(String name) {
            return original.containsHeader(name);
        }

        @Override
        public String encodeURL(String url) {
            return original.encodeURL(url);
        }

        @Override
        public String encodeRedirectURL(String url) {
            return original.encodeRedirectURL(url);
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {
            original.sendError(sc, msg);
        }

        @Override
        public void sendError(int sc) throws IOException {
            original.sendError(sc);
        }

        @Override
        public void sendRedirect(String location) throws IOException {
            original.sendRedirect(location);
        }

        @Override
        public void setDateHeader(String name, long date) {
            original.setDateHeader(name, date);
        }

        @Override
        public void addDateHeader(String name, long date) {
            original.addDateHeader(name, date);
        }

        @Override
        public void setHeader(String name, String value) {
            original.setHeader(name, value);
        }

        @Override
        public void addHeader(String name, String value) {
            original.addHeader(name, value);
        }

        @Override
        public void setIntHeader(String name, int value) {
            original.setIntHeader(name, value);
        }

        @Override
        public void addIntHeader(String name, int value) {
            original.addIntHeader(name, value);
        }

        @Override
        public String getHeader(String arg0) {
            return original.getHeader(arg0);
        }

        @Override
        public Collection<String> getHeaderNames() {
            return original.getHeaderNames();
        }

        @Override
        public Supplier<Map<String, String>> getTrailerFields() {
            return HttpServletResponse.super.getTrailerFields();
        }

        @Override
        public void setTrailerFields(Supplier<Map<String, String>> supplier) {
            HttpServletResponse.super.setTrailerFields(supplier);
        }

        @Override
        public Collection<String> getHeaders(String arg0) {
            return original.getHeaders(arg0);
        }

        @Override
        public int getStatus() {
            return original.getStatus();
        }

        @Override
        public void setStatus(int sc) {
            original.setStatus(sc);
        }
    }
}