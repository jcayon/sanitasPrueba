package com.mycorp.zendesk.integration.zendesk;

import java.io.IOException;

import com.mycorp.zendesk.exception.ZendeskException;
import com.ning.http.client.AsyncCompletionHandler;

abstract class ZendeskAsyncCompletionHandler<T> extends AsyncCompletionHandler<T> {
    @Override
    public void onThrowable(Throwable t) {
        if (t instanceof IOException) {
            throw new ZendeskException(t);
        } else {
            super.onThrowable(t);
        }
    }
}