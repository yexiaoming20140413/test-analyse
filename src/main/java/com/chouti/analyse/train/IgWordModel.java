package com.chouti.analyse.train;

/*******************************************************************************
 * Copyright (c) 2005-2016 Gozap, Inc.
 * Contributors:
 * xiaoming  on 16-11-17.
 *******************************************************************************/
public class IgWordModel {
    private Integer N1;

    private Integer N2;

    private String word;
    //统计每个词的正文档出现频率（A）
    private Integer A;
    //负文档出现频率（B）
    private Integer B;
    //正文档不出现频率(c)
    private Integer c;
    //负文档不出现频率(d)
    private Integer d;

    private Double entorypy;

    private Double infoGain;


    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Integer getA() {
        return A;
    }

    public void setA(Integer a) {
        if(null == a){
           a = 1;
        }
        A = a;
    }

    public Integer getB() {
        return B;
    }

    public void setB(Integer b) {
        if(null == b){
            b = 1;
        }
        B = b;
    }

    public Integer getC() {
        return c;
    }

    public void setC(Integer c) {
        if(null == c){
            c = 1;
        }
        this.c = c;
    }

    public Integer getD() {
        return d;
    }

    public void setD(Integer d) {
        if(null == d){
           d = 1;
        }
        this.d = d;
    }



    public Integer getN1() {
        return N1;
    }

    public void setN1(Integer n1) {
        N1 = n1;
    }

    public Integer getN2() {
        return N2;
    }

    public void setN2(Integer n2) {
        N2 = n2;
    }

    public Double getEntorypy() {
        return entorypy;
    }

    public void setEntorypy(Double entorypy) {
        this.entorypy = entorypy;
    }

    public Double getInfoGain() {
        return infoGain;
    }

    public void setInfoGain(Double infoGain) {
        this.infoGain = infoGain;
    }

    public void entorypy(){
        double d1 = (double)N1/(double)(N1+N2);
        double d2 = java.lang.Math.log(d1);
        double d3 = (double)N2 / (double)(N1+N2);
        double d4 = java.lang.Math.log(d3);
        this.entorypy = - (d1*d2+d3*d4);
    }



    public void genInfoGain(){
        entorypy();
        double d1 = ((double) A+ (double) B)/((double) N1+ (double) N2);

        double d2 = (double) A /((double) A+ (double) B);

        double d3 = java.lang.Math.log(d2);

        double d4 = (double) B /((double) A+ (double) B);

        double d5 = java.lang.Math.log(d4);

        double d6 = ((double) c + (double) d)/((double) N1+ (double) N2);

        double d7 = (double) c /((double) c+ (double) d);

        double d8 = java.lang.Math.log(d7);

        double d9 = (double) d /((double) c+ (double) d);

        double d10 = java.lang.Math.log(d9);

        this.infoGain = this.entorypy + d1 *(d2*d3+d4*d5)+d6*(d7*d8+d9*d10);
    }
}
