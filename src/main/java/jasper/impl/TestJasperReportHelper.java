package jasper.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jasper.interfaces.JasperReportHelper;
import jasper.model.TestReportModel;
import net.sf.jasperreports.engine.JRException;

public class TestJasperReportHelper implements JasperReportHelper {

	public TestJasperReportHelper() throws IOException, IllegalArgumentException, IllegalAccessException, JRException {
		
		JRGeneretSaport	jr = new JRGeneretSaport(this);
		jr.setNotNullParams(null);
		jr.start();
	}

	public List<Object> getModeReports() {
		
		return new ArrayList<Object>(Arrays.asList(new  TestReportModel()));
	}


	public String getNameReports() {
		// TODO Auto-generated method stub
		return "testJrNameReport";
	}


	public String getNameJRFile() {
		// TODO Auto-generated method stub
		return "testJr";
	}
	

}
