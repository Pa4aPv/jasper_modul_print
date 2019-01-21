package jasper.impl;

import java.io.IOException;
import java.io.InputStream;
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
import net.sf.jasperreports.engine.util.JRProperties;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

public class JRGeneretSaport {

	Path pathTempDir = Files.createTempDirectory("medregSvd").toAbsolutePath();
	JasperReportHelper completedTemplate;
	int namb = 0;
	boolean isPrintReport;
	List<String> notNullParams;

	// Map<String, Object> params = new HashMap<>();
	List<Map<String, Object>> listMap;

	public JRGeneretSaport(JasperReportHelper completedTemplate) throws IOException {
		this.completedTemplate = completedTemplate;
	}

	public void start() throws IllegalArgumentException, IllegalAccessException, IOException, JRException {
		isPrintReport = true;
		if (completedTemplate != null) {
			if (completedTemplate.getModeReports() != null) {
				setParam(completedTemplate.getModeReports());
				if (isPrintReport)
					printJReport();
				else
					printNoData();
			}
		}
	}

	private void printNoData() {
		try {
			List<String> lines = Arrays.asList("No data table");
			Path file = Paths.get(getPathTempDir().toString(),
					completedTemplate.getNameReports() + "_" + namb++ + ".txt");

			Files.write(file, lines, Charset.forName("UTF-8"));
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

	public Path getPathTempDir() {
		return pathTempDir;
	}

	/*
	 * private void createJReport() throws JRException { try { //InputStream
	 * reportStream2 = getClass().getResourceAsStream("/jasper/vmt.jrxml");
	 * InputStream reportStream2 =
	 * getClass().getResourceAsStream("/jasper/"+reportShablon.getNameJRFile()+
	 * ".jrxml"); JasperReport jasper =
	 * JasperCompileManager.compileReport(reportStream2); JasperPrint print =
	 * JasperFillManager.fillReport(jasper, params, new JREmptyDataSource());
	 * 
	 * 
	 * JRPdfExporter pd= new JRPdfExporter(); SimpleOutputStreamExporterOutput
	 * output2= new SimpleOutputStreamExporterOutput(Paths.get(getParentDir(),
	 * "Отчет_"+namb+++".pdf").toFile().getAbsolutePath()); SimpleExporterInput
	 * input2 = new SimpleExporterInput(print);
	 * 
	 * pd.setExporterInput(input2); pd.setExporterOutput(output2);
	 * 
	 * pd.exportReport();
	 * 
	 * 
	 * String odtPath = Paths.get(odtDir.toString(), reportShablon.getNameReports()+
	 * "_" + namb++ + ".odt").toString();
	 * 
	 * JROdtExporter oe = new JROdtExporter(); SimpleOutputStreamExporterOutput
	 * output = new SimpleOutputStreamExporterOutput(odtPath); SimpleExporterInput
	 * input = new SimpleExporterInput(print); oe.setExporterInput(input);
	 * oe.setExporterOutput(output); oe.exportReport();
	 * 
	 * Path path = FileTools.getTempFileName("medregSvd_", ".rar");
	 * 
	 * // ArchTools.zipFile(odtDir, path); System.out.println(odtPath); } catch
	 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); } }
	 */

	private InputStream getInputStreamJReport() {
		// C:\medreg\server\manager\target\classes\jasper
		System.out.println((InputStream) getClass()
				.getResourceAsStream("/jasper/" + completedTemplate.getNameJRFile() + ".jrxml"));
		return getClass().getResourceAsStream("/jasper/" + completedTemplate.getNameJRFile() + ".jrxml");
	}

	private SimpleOutputStreamExporterOutput getOutputStreamJReport() {

		String pdfPath = Paths
				.get(getPathTempDir().toString(), namb++ + "_" + completedTemplate.getNameReports() + ".pdf")
				.toString();
		System.out.println(pdfPath);
		return new SimpleOutputStreamExporterOutput(pdfPath);
	}

	private JasperDesign getDesignJReport() throws JRException {
		JRProperties.setProperty("net.sf.jasperreports.extension.registry.factory.fonts=net.sf.jasperreports.engine.fonts.SimpleFontExtensionsRegistryFactory\r\n" + 
				"net.sf.jasperreports.extension.simple.font.families.arialnarrowfamily", "/jasper/fonts/fonts.xml");
		JasperDesign design = JRXmlLoader.load(getInputStreamJReport());
		JRDesignStyle defaultStyle = new JRDesignStyle();
		defaultStyle.setName("default_style");
		defaultStyle.setFontName("Arial Narrow");
		defaultStyle.setDefault(true);
		design.addStyle(defaultStyle);
		design.setDefaultStyle(defaultStyle);
		return design;
	}

	private JasperReport getCompiledJrxmlFile() throws JRException {
		return JasperCompileManager.compileReport(getDesignJReport());
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
	 * Установка параметров через свойства
	 * объекта. objectParam - объект с заполненными
	 * полями.
	 */

	private void setParam(List<Object> lstObjectParam)
			throws IllegalArgumentException, IllegalAccessException, IOException {
		listMap = new ArrayList<Map<String, Object>>();
		for (Object objectParam : lstObjectParam) {
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

	/**
	 * notNullParams - список параметров которые не
	 * должны быть пустыми. Если текущий
	 * параметр Value, есть в списке, возвращаем
	 * запрет на печать документа.
	 */
	public void setNotNullParams(List<String> notNullParams) {
		this.notNullParams = notNullParams;
	}

	private boolean isMandatoryValue(String inValue) {
		// notNullParams - список параметров которые не
		// должны быть пустыми.
		// Если текущий параметр Value, есть в списке,
		// возвращаем запрет на
		// печать документа
		for (String item : notNullParams) {
			if (item.equals(inValue)) {
				return true;
			}

		}
		this.notNullParams = notNullParams;
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
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}

			/*
			 * if(checkNameField(jrField.getName(), useNameFields))
			 * 
			 * String a = jrField.getName();
			 */
			/*
			 * 
			 * switch (jrField.getName()){ case "f_name" : return row.getName(); case
			 * "s_str_num" : return row.getS_code(); case "f_s_cond" : return
			 * row.getS_cond(); case "td_n3" : return row.getTd_n3(); case "td_n4" : return
			 * row.getTd_n4(); case "td_n5" : return row.getTd_n5(); case "td_n6" : return
			 * row.getTd_n6(); case "td_n7" : return row.getTd_n7(); case "td_n8" : return
			 * row.getTd_n8(); case "td_n9" : return row.getTd_n9(); case "td_n10" : return
			 * row.getTd_n10(); default: return null; }
			 * 
			 * if (jrField.getName().equals("f_name")) return row.getName(); else if
			 * (jrField.getName().equals("f_s_cond")) return row.getS_code(); else
			 */
			return result;
		}
	}

}
