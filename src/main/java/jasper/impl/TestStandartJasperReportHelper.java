package jasper.impl;

import java.io.IOException;

import jasper.model.TestReportModel;
import net.sf.jasperreports.engine.JRException;

public class TestStandartJasperReportHelper {

	public TestStandartJasperReportHelper() throws IOException, IllegalArgumentException, IllegalAccessException, JRException {
		JasperReportGenerator jr = new JasperReportGenerator();
		jr.setParam(new TestReportModel(), "testJR", "Тестовый отчет");
		jr.start();
	}
	
	

}
