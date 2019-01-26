package jasper.impl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jasper.interfaces.JasperReportHelper;
import jasper.interfaces.ReportModel;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

public class JasperReportGenerator {

	private List<ReportModel> reportModels;
	private String nameReport;
	private String nameJRFile;
	private List<String> notNullParams;
	private int namb = 0;
	private boolean isPrintReport;
	private Path pathTempDir = Files.createTempDirectory("medregSvd").toAbsolutePath();
	private List<Map<String, Object>> listMap;

	/**
	 * конструктор - стандартная версия формирования отчетов, параметры задаются через setParam;
	 */
	public 
	JasperReportGenerator() throws IOException {
	}

	/**
	 * конструктор - поддержка старой версии формирования отчетов, параметры
	 * задаются через интерфейс JasperReportHelper;
	 */
	public JasperReportGenerator(JasperReportHelper completedTemplate) throws IOException {
		if (completedTemplate == null) {
			throw new NullPointerException("null JasperReportHelper");
		}
		nameReport = completedTemplate.getNameReports();
		nameJRFile = completedTemplate.getNameJRFile();
		reportModels = completedTemplate.getModelReports();

	}

	/**
	 * задает параметры для группы оточетов: 1) группа моделей отчета с заполненными полями;
	 * 2) имя файла jrxml Шаблона(должны хранится в директории -resources\jasper\;
	 * 3) название отчета.
	 */
	public void setParam(List<ReportModel> reportModels, String nameJRFile, String nameReport ) {
		this.reportModels = reportModels;
		this.nameReport = nameReport;
		this.nameJRFile = nameJRFile;

	}

	/**
	 * задает параметры для одного отчета: 1) модель отчета с заполненными полями;
	 * 2) имя файла jrxml Шаблона(должны хранится в директории -resources\jasper\;
	 * 3) название отчета.
	 */
	public void setParam(ReportModel reportModel, String nameJRFile, String nameReport) {
		reportModels = new ArrayList<ReportModel>();
		reportModels.add(reportModel);
		this.nameReport = nameReport;
		this.nameJRFile = nameJRFile;

	}

	/**
	 * возвращает temp директорию где хранятся отчеты, для дальнейшей архивации
	 */
	public Path getPathTempDir() {
		return pathTempDir;
	}

	/**
	 * notNullParams - список параметров которые не должны быть пустыми. Если
	 * обязательные параметры пустые, создается документ об ошибке.
	 */
	public void setNotNullParams(List<String> notNullParams) {
		this.notNullParams = notNullParams;
	}

	public void start() throws IllegalArgumentException, IllegalAccessException, IOException, JRException {

		if (reportModels == null) {
			throw new NullPointerException("null List<ReportModel>");
		}

		if (nameReport == null) {
			throw new NullPointerException("null NameReport");
		}

		if (nameReport == null) {
			throw new NullPointerException("null NameJRFile");
		}

		isPrintReport = true;
		setListMap(reportModels);
		if (isPrintReport)
			printJReport();
		else
			printNoData();

	}

	private void printNoData() {
		try {
			List<String> lines = Arrays.asList("No data table");
			Path file = Paths.get(getPathTempDir().toString(), nameReport + "_" + namb++ + ".txt");
			Files.write(file, lines, Charset.forName("UTF-8"));
			System.out.println(file.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void printJReport() throws JRException {

		JRPdfExporter pf = new JRPdfExporter();
		pf.setExporterInput(SimpleExporterInput.getInstance(getListJasperPrintJReport()));
		pf.setExporterOutput(getOutputStreamJReport());
		pf.exportReport();

	}

	private SimpleOutputStreamExporterOutput getOutputStreamJReport() {
		String pdfPath = Paths.get(getPathTempDir().toString(), namb++ + "_" + nameReport + ".pdf").toString();
		System.out.println(pdfPath);
		//pdf можно возвращать так//System.out.println(Paths.get(nameReport + ".pdf").toString());
		return new SimpleOutputStreamExporterOutput(pdfPath);
	}

	private JasperReport getCompiledJrxmlFile() throws JRException {
		JasperDesign design = JRXmlLoader.load(getClass().getResourceAsStream("/jasper/" + nameJRFile + ".jrxml"));
		JRDesignStyle defaultStyle = new JRDesignStyle();
		defaultStyle.setName("default_style");
		defaultStyle.setFontName("Arial Narrow");
		defaultStyle.setDefault(true);
		design.addStyle(defaultStyle);
		design.setDefaultStyle(defaultStyle);
		return JasperCompileManager.compileReport(design);
	}

	private List<JasperPrint> getListJasperPrintJReport() throws JRException {
		List<JasperPrint> jpList = new ArrayList<JasperPrint>();
		for (Map<String, Object> params : listMap) {
			JasperPrint print = JasperFillManager.fillReport(getCompiledJrxmlFile(), params, new JREmptyDataSource());
			jpList.add(print);
		}

		return jpList;
	}

	/**
	 * Установка параметров через свойства объекта. objectParam - объект с
	 * заполненными полями.
	 */

	private void setListMap(List<ReportModel> lstObjectParam)
			throws IllegalArgumentException, IllegalAccessException, IOException {
		listMap = new ArrayList<Map<String, Object>>();
		for (ReportModel objectParam : lstObjectParam) {
			Map<String, Object> params = new HashMap<String, Object>();
			String nameParam = null;
			String valueParam = null;
			List<Object> tableData = null;
			Boolean isNullParams;
			Field[] fields = objectParam.getClass().getFields();
			for (Field field : fields) {
				if (field.get(objectParam) != null) {
					isNullParams = false;

					nameParam = field.getName();

					if (field.get(objectParam) instanceof List) {
						tableData = (List<Object>) field.get(objectParam);
						if (tableData.size() != 0) {
							params.put(nameParam, new ListDataSource(tableData));
						} else {
							isNullParams = true;
						}

					} else {
						valueParam = field.get(objectParam).toString();
						if (valueParam != null) {
							params.put(nameParam, valueParam);
						} else {
							isNullParams = true;
						}
					}

					if (notNullParams != null)
						if (isMandatoryValue(nameParam) && isNullParams)
							isPrintReport = false;
				}
			}
			listMap.add(params);
		}
	}

	private boolean isMandatoryValue(String inValue) {
		for (String item : notNullParams) {
			if (item.equals(inValue)) {
				return true;
			}

		}
		//this.notNullParams = notNullParams;
		return false;
	}

	private class ListDataSource implements JRDataSource {
		private List<Object> list;
		Field[] fields;
		private int idx;
		private int idxFild;

		public ListDataSource(List<Object> tableData) {
			this.list = tableData;
			this.idx = 0;
			fields = list.get(0).getClass().getFields();
		}

		public boolean next() throws JRException {
			boolean hasValues = idx < list.size();

			if (hasValues) {
				fields = list.get(idx).getClass().getFields();
				idxFild = idx;
				idx++;
			}

			return hasValues;
		}

		public Object getFieldValue(JRField jrField) throws JRException {
			Object result = null;
			String a = jrField.getName();

			for (Field field : fields) {
				if (a.equals(field.getName())) {

					try {
						result = field.get(list.get(idxFild));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}

				}

			}
			return result;
		}
	}

}
