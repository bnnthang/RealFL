package com.bnnthang.fltestbed.commonutils.clients;

import java.io.Serializable;

public interface IClientNetworkStatManager extends Serializable {
    void increaseBytes(Long x);
    void increaseCommTime(Double x);
    void newRound();
    Long getBytes(Integer round);
    Double getCommTime(Integer round);
    Integer getRounds();
}
