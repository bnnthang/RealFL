package com.bnnthang.fltestbed.commonutils.clients;

import java.io.Serializable;

public interface IClientTrainingStatManager extends Serializable {
    void newRound();
    void setTrainingTime(Double elapsedTime);
    Double getTrainingTime(Integer round);
    Integer getRounds();
}
