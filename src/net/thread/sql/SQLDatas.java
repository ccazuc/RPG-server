package net.thread.sql;

public class SQLDatas {

	protected int iValue1;
	protected int iValue2;
	protected int iValue3;
	protected long lValue1;
	protected long lValue2;
	protected byte bValue1;
	protected String stringValue1;
	protected String stringValue2;
	
	public SQLDatas(int iValue1) {
		this.iValue1 = iValue1;
	}
	
	public SQLDatas(int iValue1, int iValue2) {
		this.iValue1 = iValue1;
		this.iValue2 = iValue2;
	}
	
	public SQLDatas(int iValue1, long lValue1) {
		this.iValue1 = iValue1;
		this.lValue1 = lValue1;
	}
	
	public SQLDatas(int iValue1, byte bValue1) {
		this.iValue1 = iValue1;
		this.bValue1 = bValue1;
	}
	
	public SQLDatas(int iValue1, String stringValue1) {
		this.iValue1 = iValue1;
		this.stringValue1 = stringValue1;
	}
	
	public SQLDatas(int iValue1, int iValue2, int iValue3) {
		this.iValue1 = iValue1;
		this.iValue2 = iValue2;
		this.iValue3 = iValue3;
	}
	
	public SQLDatas(int iValue1, int iValue2, long lValue1) {
		this.iValue1 = iValue1;
		this.iValue2 = iValue2;
		this.lValue1 = lValue1;
	}
	
	public SQLDatas(int iValue1, int iValue2, int iValue3, String text) {
		this.iValue1 = iValue1;
		this.iValue2 = iValue2;
		this.iValue3 = iValue3;
		this.stringValue1 = text;
	}
	
	public SQLDatas(long lValue1, long lValue2, String stringValue1, String stringValue2) {
		this.lValue1 = lValue1;
		this.lValue2 = lValue2;
		this.stringValue1 = stringValue1;
		this.stringValue2 = stringValue2;
	}
	
	public SQLDatas(int iValue1, long lValue1, long lValue2, String stringValue1, String stringValue2) {
		this.iValue1 = iValue1;
		this.lValue1 = lValue1;
		this.lValue2 = lValue2;
		this.stringValue1 = stringValue1;
		this.stringValue2 = stringValue2;
	}
	
	public int getIValue1() {
		return this.iValue1;
	}
	
	public int getIValue2() {
		return this.iValue2;
	}
	
	public int getIValue3() {
		return this.iValue3;
	}
	
	public long getLValue1() {
		return this.lValue1;
	}
	
	public long getLValue2() {
		return this.lValue2;
	}
	
	public byte getBValue1() {
		return this.bValue1;
	}
	
	public String getStringValue1() {
		return this.stringValue1;
	}
	
	public String getStringValue2() {
		return this.stringValue2;
	}
}
