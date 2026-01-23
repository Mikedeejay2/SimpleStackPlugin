package com.mikedeejay2.simplestack.bytecode;

import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface MethodVisitorValidator {
    void start();
    void marker(String value);
    void end();

    final class Impl implements MethodVisitorValidator {
        final MethodVisitor mv;
        final List<String> expected;
        final List<String> actual;

        public Impl(MethodVisitor mv, String... expected) {
            this.mv = mv;
            this.expected = Arrays.asList(expected);
            this.actual = new ArrayList<>();
        }

        @Override
        public void start() {
            actual.clear();
        }

        @Override
        public void marker(String value) {
            int idx = actual.size();
            actual.add(value);
            if(actual.size() > expected.size()) {
                throw new TooManyMarkersException(this, value);
            }
            if(!expected.get(idx).equals(value)) {
                throw new WrongOrderMarkerException(this, value);
            }
        }

        @Override
        public void end() {
            if(!actual.equals(expected)) {
                throw new MissingMarkerException(this);
            }
        }
    }

    enum NoOp implements MethodVisitorValidator {
        INSTANCE;

        @Override
        public void start() {}

        @Override
        public void marker(String value) {}

        @Override
        public void end() {}
    }

    class MissingMarkerException extends MethodVisitorValidatorException {
        public MissingMarkerException(MethodVisitorValidator.Impl validator) {
            super(validator, "Missing one or more markers");
        }
    }

    class WrongOrderMarkerException extends MethodVisitorValidatorException {
        public WrongOrderMarkerException(MethodVisitorValidator.Impl validator, String value) {
            super(validator, "Wrong order of markers detected when passing value: " + value);
        }
    }

    class TooManyMarkersException extends MethodVisitorValidatorException {
        public TooManyMarkersException(MethodVisitorValidator.Impl validator, String value) {
            super(validator, "Too many markers detected when passing value: " + value);
        }
    }

    class MethodVisitorValidatorException extends RuntimeException {
        public MethodVisitorValidatorException(MethodVisitorValidator.Impl validator, String message) {
            super("MethodVisitorValidator: " + message +
                      "\nMethodVisitor: " + validator.mv.getClass().getName() +
                      "\nExpected markers: " + validator.expected +
                      "\nActual markers: " + validator.actual);
        }
    }
}
