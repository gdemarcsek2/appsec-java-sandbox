package com.gdemarcsek.appsec.visibility.demo.core;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

import lombok.Value;

@JsonIgnoreType
@Value
public class Sensitive<T> implements Externalizable {
      private transient final AtomicReference<T> value;
      private final RedactionStrategy<T> redactor;

      public Sensitive(final T val, final RedactionStrategy<T> r) {
            this.value = new AtomicReference<T>(val);
            this.redactor = r;
      }

      public Optional<T> get() {
            return Optional.ofNullable(value.getAndSet(null));
      }

      @Override
      public void writeExternal(ObjectOutput out) throws IOException {
            throw new RuntimeException("Sensitive objects cannot be serialized.");
      }

      @Override
      public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            throw new RuntimeException("Sensitive objects cannot be deserialized.");
      }

      public String toString() {
            return this.redactor.redact(this.value.get()).toString();
      }
}
