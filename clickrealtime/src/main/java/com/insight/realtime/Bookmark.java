package com.insight.realtime;

import java.sql.Timestamp;
import java.util.List;
/*
 * Class Bookmark is a mapping of the raw JSON data record
 */
public class Bookmark implements java.io.Serializable{
	public String a; //userAgent;
    public String c; //countryCode;
    public Boolean nk; //knownUser;
    public String tz; //tZone;
    public String g; //globalHash;
    public String h; //userHash;
    public String l; //userLogin;
    public String hh; //shortUrl-cname;
    public String r; //referringUrl;
    public String u; //longUrl;
    public Timestamp t; //clickTime;
    public String gr; //geoRegion;
    public List<Double>ll ; //latLongitude;
    public String cy; // geoCityName;
    
    public Long hc; //hashCreated;
    public String al; //acceptLang;
	public String getA() {
		return a;
	}
	public void setA(String a) {
		this.a = a;
	}
	public String getC() {
		return c;
	}
	public void setC(String c) {
		this.c = c;
	}
	public Boolean getNk() {
		return nk;
	}
	public void setNk(Boolean nk) {
		this.nk = nk;
	}
	public String getTz() {
		return tz;
	}
	public void setTz(String tz) {
		this.tz = tz;
	}
	public String getG() {
		return g;
	}
	public void setG(String g) {
		this.g = g;
	}
	public String getH() {
		return h;
	}
	public void setH(String h) {
		this.h = h;
	}
	public String getL() {
		return l;
	}
	public void setL(String l) {
		this.l = l;
	}
	public String getHh() {
		return hh;
	}
	public void setHh(String hh) {
		this.hh = hh;
	}
	public String getR() {
		return r;
	}
	public void setR(String r) {
		this.r = r;
	}
	public String getU() {
		return u;
	}
	public void setU(String u) {
		this.u = u;
	}
	public Timestamp getT() {
		return t;
	}
	public void setT(Timestamp t) {
		this.t = t;
	}
	public String getGr() {
		return gr;
	}
	public void setGr(String gr) {
		this.gr = gr;
	}
	public List<Double> getLl() {
		return ll;
	}
	public void setLl(List<Double> ll) {
		this.ll = ll;
	}
	public String getCy() {
		return cy;
	}
	public void setCy(String cy) {
		this.cy = cy;
	}
	public Long getHc() {
		return hc;
	}
	public void setHc(Long hc) {
		this.hc = hc;
	}
	public String getAl() {
		return al;
	}
	public void setAl(String al) {
		this.al = al;
	}
	public Bookmark(String a, String c, Boolean nk, String tz, String g,
			String h, String l, String hh, String r, String u, Timestamp t,
			String gr, List<Double> ll, String cy, Long hc, String al) {
		
		this.a = a;
		this.c = c;
		this.nk = nk;
		this.tz = tz;
		this.g = g;
		this.h = h;
		this.l = l;
		this.hh = hh;
		this.r = r;
		this.u = u;
		this.t = t;
		this.gr = gr;
		this.ll = ll;
		this.cy = cy;
		this.hc = hc;
		this.al = al;
	}
    
    @Override
	public String toString() {
		return "Bookmark [a=" + a + ", c=" + c + ", nk=" + nk + ", tz=" + tz
				+ ", g=" + g + ", h=" + h + ", l=" + l + ", hh=" + hh + ", r="
				+ r + ", u=" + u + ", t=" + t + ", gr=" + gr + ", ll=" + ll
				+ ", cy=" + cy + ", hc=" + hc + ", al=" + al + "]";
	}
    
    
   	public String toCustomString() {
   		return "[ h=" + h + ",  u=" + u + "]";
   	}
	public Bookmark(){}
}
