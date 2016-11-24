package net.thread.sql;

public class SQLDatas {

	protected int iValue1;
	protected int iValue2;
	protected int iValue3;
	protected long lValue1;
	protected long lValue2;
	protected String text;
	
	public SQLDatas(int iValue1) {
		this.iValue1 = iValue1;
	}
	
	public SQLDatas(int iValue1, String text) {
		this.iValue1 = iValue1;
		this.text = text;
	}
	
	public SQLDatas(int iValue1, int iValue2) {
		this.iValue1 = iValue1;
		this.iValue2 = iValue2;
	}
	
	public SQLDatas(int iValue1, int iValue2, int iValue3) {
		this.iValue1 = iValue1;
		this.iValue2 = iValue2;
		this.iValue3 = iValue3;
	}
	
	public SQLDatas(int iValue1, int iValue2, int iValue3, String text) {
		this.iValue1 = iValue1;
		this.iValue2 = iValue2;
		this.iValue3 = iValue3;
		this.text = text;
	}
	
	public SQLDatas(int iValue1, long lValue1) {
		this.iValue1 = iValue1;
		this.lValue1 = lValue1;
	}
	
	public SQLDatas(int iValue1, int iValue2, long lValue1) {
		this.iValue1 = iValue1;
		this.iValue2 = iValue2;
		this.lValue1 = lValue1;
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
	
	public long getLValue() {
		return this.lValue1;
	}
	
	public String getText() {
		return this.text;
	}
}
