package jasper.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jasper.interfaces.JasperReportHelper;
import jasper.interfaces.ReportModel;
import jasper.model.TestReportModel;
import net.sf.jasperreports.engine.JRException;

public class TestJasperReportHelper implements JasperReportHelper {

	public TestJasperReportHelper() throws IOException, IllegalArgumentException, IllegalAccessException, JRException {
		JasperReportGenerator jr = new JasperReportGenerator(this);
		jr.setNotNullParams(null);
		jr.start();
	}

	public List<ReportModel> getModelReports() {
		return new ArrayList<ReportModel>(Arrays.asList(new TestReportModel()));
	}

	public String getNameReports() {
		return "testJrNameReport";
	}

	public String getNameJRFile() {
		return "testJr";
	}

}
