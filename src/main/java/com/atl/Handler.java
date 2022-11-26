package com.atl;

public interface Handler {
    Handler NOT_FOUND = new NotFound();
}

class NotFound implements Handler {}
