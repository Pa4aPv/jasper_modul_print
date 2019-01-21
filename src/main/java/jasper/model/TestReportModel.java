package jasper.model;

import jasper.interfaces.JasperReportModel;

public class TestReportModel implements JasperReportModel {

	public String name = "раз раз раз :)";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
