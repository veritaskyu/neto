package com.veritasware.neto.test;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chacker on 2016-11-03.
 */
class TT {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

public class StreamFilterTest {



    public static void main(String[] args) {
        Set<TT> sets = new HashSet<>();

        for (int i = 0; i < 1000000; i++) {
            TT tt = new TT();
            tt.setId("idid"+i);
            sets.add(tt);
        }

        long start = System.currentTimeMillis();

        sets.stream().filter(t -> t.getId().equals("idid99928")).forEach(
                t -> {
                    long end = System.currentTimeMillis();

                    System.out.println(t.getId());
                    System.out.println( "실행 시간 : " + ( end - start )/1000.0 );
                }
        );

    }

}
