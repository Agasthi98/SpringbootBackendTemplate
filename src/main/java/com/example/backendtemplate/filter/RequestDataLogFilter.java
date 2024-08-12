package com.example.backendtemplate.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.util.io.TeeOutputStream;
import org.json.JSONObject;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Order(1)
@RequiredArgsConstructor
public class RequestDataLogFilter extends OncePerRequestFilter {

    private static final Logger logWriter = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final ObjectMapper objectMapper;


    @Override
    protected void doFilterInternal(final @NonNull HttpServletRequest request, final @NonNull HttpServletResponse response, final @NonNull FilterChain filterChain) {
        Instant start = Instant.now();
        final String requestId = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        MDC.put("req-id", requestId);
        try {
            boolean allowSkippingRequest = skipRequestLogging(request);
            if (allowSkippingRequest) {
                filterChain.doFilter(request, response);
                return;
            }

            BufferedRequestWrapper bufferedRequest = new BufferedRequestWrapper(request);
            BufferedResponseWrapper bufferedResponse = new BufferedResponseWrapper(response);
            String remoteAddress = request.getHeader("X-Forwarded-For");

            HashMap<String, Object> requestLogMessage = new HashMap<>();
            requestLogMessage.put("HTTP METHOD", request.getMethod());
            requestLogMessage.put("PATH", request.getServletPath());
            requestLogMessage.put("REMOTE ADDRESS", StringUtils.hasLength(remoteAddress) ? remoteAddress.split(",")[0].trim() : request.getRemoteAddr());

            validateJson(requestLogMessage, bufferedRequest, request);

            logWriter.log(Level.INFO, "Request: {0}", objectMapper.writeValueAsString(requestLogMessage));

            filterChain.doFilter(bufferedRequest, bufferedResponse);

            JSONObject responseObject = new JSONObject(bufferedResponse.getContent());
            //skip logging data object
            boolean allowSkippingResponse = skipResponseLogging(request);
            if (allowSkippingResponse && request.getMethod().equals("POST")) {
                responseObject.put("data", "Data length - " + responseObject.get("data").toString().length());
            }
            logWriter.log(Level.INFO, "Response: {0}", objectMapper.writeValueAsString(responseObject.toMap()));

        } catch (Exception e) {
            logWriter.log(Level.SEVERE, e, () -> "Exception [request-logger-filter]: " + e.getMessage());
        } finally {
            Instant finish = Instant.now();
            long time = Duration.between(start, finish).toMillis();
            logWriter.log(Level.INFO, () -> "Time spent to respond: " + time + " ms");
            MDC.remove("req-id");
        }
    }

    private boolean skipRequestLogging(HttpServletRequest httpServletRequest) {
        //Add request path if you want to bypass writing the request body to log
        String[] regs = {
                "/resource/(.*?)",
        };
        Matcher matcher;
        for (String pathExpr : regs) {
            matcher = Pattern.compile(pathExpr).matcher(httpServletRequest.getServletPath());
            if (matcher.find()) {
                logWriter.info("Request: PATH: " + httpServletRequest.getServletPath());
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

    public static class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {

        private final String body;

        public CustomHttpServletRequestWrapper(HttpServletRequest request) {
            super(request);
            body = getRequestBody(request);
        }

        private String getRequestBody(HttpServletRequest request) {
            try {
                return request.getReader().lines().collect(Collectors.joining());
            } catch (IOException e) {
                // Handle exception
            }
            return "";
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
            return new ServletInputStream() {
                @Override
                public boolean isFinished() {
                    return false;
                }

                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setReadListener(ReadListener listener) {
                    // Not used
                }

                public int read() throws IOException {
                    return byteArrayInputStream.read();
                }
            };
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(this.getInputStream()));
        }

    }

    public static class CustomHttpServletResponseWrapper extends HttpServletResponseWrapper {

        private final ByteArrayOutputStream outputStream;

        public CustomHttpServletResponseWrapper(HttpServletResponse response) {
            super(response);
            outputStream = new ByteArrayOutputStream();
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return new ServletOutputStream() {
                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setWriteListener(WriteListener listener) {
                    // Not used
                }

                @Override
                public void write(int b) throws IOException {
                    outputStream.write(b);
                }
            };
        }

        public byte[] getResponseBytes() {
            return outputStream.toByteArray();
        }

        public void flushResponse() throws IOException {
            getResponse().flushBuffer();
        }
    }


    private static final class BufferedRequestWrapper extends HttpServletRequestWrapper {

        private final ByteArrayOutputStream baos;
        private final byte[] buffer;

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
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.buffer);
            return new BufferedServletInputStream(byteArrayInputStream);
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
            //Later use
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

        @Override
        public void flush() throws IOException {
            super.flush();
            this.targetStream.flush();
        }

        @Override
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
            //Later use
        }
    }

    private void validateJson(HashMap<String, Object> requestLogMessage, BufferedRequestWrapper bufferedRequest, HttpServletRequest request) {
        try {
            JSONObject requestDataObject = new JSONObject(bufferedRequest.getRequestBody());
            requestLogMessage.put("REQUEST BODY", requestDataObject.toMap());
        } catch (Exception e) {
            if (!request.getMethod().equals("GET")) {
                logWriter.info("REQUEST BODY: Not a valid JSON string");
            }
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