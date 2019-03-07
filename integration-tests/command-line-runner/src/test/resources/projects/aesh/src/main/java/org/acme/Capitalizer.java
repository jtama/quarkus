package org.acme;

import javax.inject.Named;
import javax.inject.Singleton;


@Singleton
@Named("cap")
public class Capitalizer {

    public String perform(String input) {
        return input.toUpperCase();
    }

}
