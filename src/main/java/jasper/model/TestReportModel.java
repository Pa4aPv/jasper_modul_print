package jasper.model;

import jasper.interfaces.ReportModel;

public class TestReportModel implements ReportModel {
	
	public	String name = "Женя дугачек, курит табачек :)";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
