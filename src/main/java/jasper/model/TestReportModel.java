package jasper.model;

import jasper.interfaces.ReportModel;

public class TestReportModel implements ReportModel {
	
	public	String name = "раз раз раз :)";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
