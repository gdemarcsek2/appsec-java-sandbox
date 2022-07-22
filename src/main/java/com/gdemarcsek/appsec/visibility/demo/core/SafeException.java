package com.gdemarcsek.appsec.visibility.demo.core;

import com.google.errorprone.annotations.CompileTimeConstant;

public class SafeException extends Exception {
    public SafeException() {
        super();
    }

    public SafeException(@CompileTimeConstant String message) {
        super(message);
    }
}
