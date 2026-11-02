package xyz.refinedev.phoenix.utils.auth.yubikey.api;

import xyz.refinedev.lib.sl4j.Logger;
import xyz.refinedev.lib.sl4j.LoggerFactory;
import xyz.refinedev.phoenix.utils.auth.yubikey.VerificationResponseImpl;
import xyz.refinedev.phoenix.utils.auth.yubikey.exception.YubicoVerificationException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class VerificationService {

    private final ExecutorCompletionService<VerificationResponse> executorCompletionService;

    private static final long timeTime = 250L;

    public VerificationService() {
        ThreadPoolExecutor threadPoolExecutor =
                new ThreadPoolExecutor(
                        0, 100, timeTime, TimeUnit.MILLISECONDS, new SynchronousQueue<>());
        this.executorCompletionService = new ExecutorCompletionService<>(threadPoolExecutor);
    }

    @Deprecated
    public VerificationResponse findVerificationResponse(List<String> values, String text)
            throws YubicoVerificationException {
        return this.findVerificationResponse(values, text, 5);
    }

    public VerificationResponse findVerificationResponse(
            List<String> values, String text, int value) throws YubicoVerificationException {
        if (value < 0) {
            throw new IllegalArgumentException("negative maxRetries is not valid.");
        }
        ArrayList values2 = new ArrayList();
        for (String text2 : values) {
            values2.add(this.executorCompletionService.submit(this.create(text, text2, value)));
        }
        VerificationResponse verificationResponse = null;
        try {
            int value2 = 0;
            Throwable throwable = null;
            Future future;
            for (future = this.executorCompletionService.poll(1L, TimeUnit.MINUTES);
                    future != null;
                    future = this.executorCompletionService.poll(1L, TimeUnit.MINUTES)) {
                try {
                    value2++;
                    values2.remove(future);
                    if (!(verificationResponse = (VerificationResponse) future.get())
                                    .getStatus()
                                    .equals(ResponseStatus.REPLAYED_REQUEST)
                            && !verificationResponse
                                    .getStatus()
                                    .equals(ResponseStatus.BACKEND_ERROR)) {
                        break;
                    }
                } catch (CancellationException cancellationException) {
                    value2--;
                } catch (ExecutionException executionException) {
                    throwable = executionException.getCause();
                }
                if (value2 >= values.size()) {
                    break;
                }
            }
            if (future == null || verificationResponse == null) {
                if (throwable != null) {
                    throw new YubicoVerificationException(
                            "Exception while executing validation.", throwable);
                }
                throw new YubicoVerificationException("Validation timeout.");
            }
        } catch (InterruptedException interruptedException) {
            throw new YubicoVerificationException("Validation interrupted.", interruptedException);
        }
        Iterator iterator2 = values2.iterator();
        while (iterator2.hasNext()) {
            ((Future) iterator2.next()).cancel(true);
        }
        return verificationResponse;
    }

    protected VerificationService.VerificationRequest create(String text, String text2, int value) {
        return new VerificationService.VerificationRequest(text2, text, value);
    }

    static class VerificationRequest implements Callable<VerificationResponse> {

        private final Logger logger =
                LoggerFactory.getLogger(VerificationService.VerificationRequest.class);

        private final String text;

        private final String textString;

        private final int count;

        private static final long time = 500L;

        public VerificationRequest(String text, String textString, int count) {
            if (count < 0) {
                throw new IllegalArgumentException("negative maxRetries is not valid.");
            }
            this.text = text;
            this.textString = textString;
            this.count = count;
        }

        public VerificationResponse call() throws Exception {
            URL uRL = new URL(this.text);
            try {
                return new VerificationResponseImpl(this.findInputStream(uRL));
            } catch (IOException oException) {
                this.logger.warn("Exception when requesting {}.", uRL.getHost(), oException);
                throw oException;
            }
        }

        protected InputStream findInputStream(URL uRL) throws IOException {
            int value = 0;
            while (true) {
                try {
                    HttpURLConnection httpURLConnection;
                    (httpURLConnection = (HttpURLConnection) uRL.openConnection())
                            .setRequestProperty("User-Agent", this.textString);
                    httpURLConnection.setConnectTimeout(15000);
                    httpURLConnection.setReadTimeout(15000);
                    return httpURLConnection.getInputStream();
                } catch (IOException oException) {
                    this.logger.warn(
                            "Exception when requesting {}, retrying.", uRL.getHost(), oException);
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException interruptedException) {
                    }
                    if (++value > this.count) {
                        throw oException;
                    }
                }
            }
        }
    }
}
