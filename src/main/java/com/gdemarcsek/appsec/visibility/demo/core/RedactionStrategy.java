package com.gdemarcsek.appsec.visibility.demo.core;

public interface RedactionStrategy<T> {
      public T redact(final T v);
}