package modelo.hibernate;
// Generated 13-ene-2014 23:25:12 by Hibernate Tools 3.2.1.GA


import java.util.HashSet;
import java.util.Set;

/**
 * Depart generated by hbm2java
 */
public class Depart  implements java.io.Serializable {


     private byte deptNo;
     private String dnombre;
     private String loc;
     private Set emples = new HashSet(0);

    public Depart() {
    }

	
    public Depart(byte deptNo, String dnombre) {
        this.deptNo = deptNo;
        this.dnombre = dnombre;
    }
    public Depart(byte deptNo, String dnombre, String loc, Set emples) {
       this.deptNo = deptNo;
       this.dnombre = dnombre;
       this.loc = loc;
       this.emples = emples;
    }
   
    public byte getDeptNo() {
        return this.deptNo;
    }
    
    public void setDeptNo(byte deptNo) {
        this.deptNo = deptNo;
    }
    public String getDnombre() {
        return this.dnombre;
    }
    
    public void setDnombre(String dnombre) {
        this.dnombre = dnombre;
    }
    public String getLoc() {
        return this.loc;
    }
    
    public void setLoc(String loc) {
        this.loc = loc;
    }
    public Set getEmples() {
        return this.emples;
    }
    
    public void setEmples(Set emples) {
        this.emples = emples;
    }

    @Override
    public String toString() {
        return dnombre;
    }
}

