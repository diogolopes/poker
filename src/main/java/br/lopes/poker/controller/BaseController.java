package br.lopes.poker.controller;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import java.util.Collection;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class BaseController {

    protected <T> ResponseEntity<T> createResponse(final T instance) {
        if (instance == null || (instance instanceof Collection && ((Collection<T>) instance).isEmpty())) {
            return new ResponseEntity<T>(NO_CONTENT);
        }
        return new ResponseEntity<T>(instance, OK);
    }

    protected <T> ResponseEntity<T> createResponse(final T instance, final HttpStatus status) {
        return new ResponseEntity<T>(instance, status);
    }
}
