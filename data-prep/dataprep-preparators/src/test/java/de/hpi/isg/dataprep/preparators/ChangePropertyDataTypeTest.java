package de.hpi.isg.dataprep.preparators;

import de.hpi.isg.dataprep.exceptions.MetadataNotFoundException;
import de.hpi.isg.dataprep.model.repository.ErrorRepository;
import de.hpi.isg.dataprep.model.target.errorlog.ErrorLog;
import de.hpi.isg.dataprep.model.target.system.Preparation;
import de.hpi.isg.dataprep.model.target.errorlog.PipelineErrorLog;
import de.hpi.isg.dataprep.model.target.errorlog.PreparationErrorLog;
import de.hpi.isg.dataprep.model.target.preparator.Preparator;
import de.hpi.isg.dataprep.util.DatePattern;
import de.hpi.isg.dataprep.util.DataType;
import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lan Jiang
 * @since 2018/8/9
 */
public class ChangePropertyDataTypeTest extends PreparatorTest {

    @Test
    public void testChangeToStringErrorLog() throws Exception {
        Preparator preparator = new ChangeDataType("identifier", DataType.PropertyType.STRING);

        Preparation preparation = new Preparation(preparator);
        pipeline.addPreparation(preparation);
        pipeline.executePipeline();

        List<ErrorLog> trueErrorlogs = new ArrayList<>();
        ErrorRepository trueErrorRepository = new ErrorRepository(trueErrorlogs);
        pipeline.getRawData().show();

        Assert.assertEquals(trueErrorRepository, pipeline.getErrorRepository());
    }

    @Test
    public void testChangeToIntegerErrorLog() throws Exception {
        Preparator preparator = new ChangeDataType("id", DataType.PropertyType.STRING, DataType.PropertyType.INTEGER);

        Preparation preparation = new Preparation(preparator);
        pipeline.addPreparation(preparation);
        pipeline.executePipeline();

        List<ErrorLog> trueErrorlogs = new ArrayList<>();

        ErrorLog pipelineErrorLog = new PipelineErrorLog(pipeline,
                new MetadataNotFoundException(String.format("The metadata %s not found in the repository.", "PropertyDataType{" +
                        "propertyName='" + "id" + '\'' +
                        ", propertyDataType=" + DataType.PropertyType.STRING +
                        '}')));
        trueErrorlogs.add(pipelineErrorLog);

        ErrorLog errorLog1 = new PreparationErrorLog(preparation, "three", new NumberFormatException("For input string: \"three\""));
        ErrorLog errorLog2 = new PreparationErrorLog(preparation, "six", new NumberFormatException("For input string: \"six\""));
        ErrorLog errorLog3 = new PreparationErrorLog(preparation, "ten", new NumberFormatException("For input string: \"ten\""));

        trueErrorlogs.add(errorLog1);
        trueErrorlogs.add(errorLog2);
        trueErrorlogs.add(errorLog3);
        ErrorRepository trueErrorRepository = new ErrorRepository(trueErrorlogs);

        pipeline.getRawData().show();

        Assert.assertEquals(trueErrorRepository, pipeline.getErrorRepository());
    }

    @Test
    public void testChangeToDoubleErrorLog() throws Exception {
        Preparator preparator = new ChangeDataType("id", DataType.PropertyType.DOUBLE);

        Preparation preparation = new Preparation(preparator);
        pipeline.addPreparation(preparation);
        pipeline.executePipeline();

        List<ErrorLog> trueErrorlogs = new ArrayList<>();
        ErrorLog errorLog1 = new PreparationErrorLog(preparation, "three", new NumberFormatException("For input string: \"three\""));
        ErrorLog errorLog2 = new PreparationErrorLog(preparation, "six", new NumberFormatException("For input string: \"six\""));
        ErrorLog errorLog3 = new PreparationErrorLog(preparation, "ten", new NumberFormatException("For input string: \"ten\""));

        trueErrorlogs.add(errorLog1);
        trueErrorlogs.add(errorLog2);
        trueErrorlogs.add(errorLog3);
        ErrorRepository trueErrorRepository = new ErrorRepository(trueErrorlogs);

        pipeline.getRawData().show();

        Assert.assertEquals(trueErrorRepository, pipeline.getErrorRepository());
    }

    @Test
    public void testChangeToDateErrorLog() throws Exception {
        Preparator preparator = new ChangeDataType("date", DataType.PropertyType.DATE,
                DatePattern.DatePatternEnum.YearMonthDay, DatePattern.DatePatternEnum.MonthDayYear);

        Preparation preparation = new Preparation(preparator);
        pipeline.addPreparation(preparation);
        pipeline.executePipeline();

        List<ErrorLog> trueErrorlogs = new ArrayList<>();
        ErrorLog errorLog1 = new PreparationErrorLog(preparation, "thisIsDate", new ParseException("Unparseable date: \"thisIsDate\"", 0));
        ErrorLog errorLog2 = new PreparationErrorLog(preparation, "12-11-1989", new ParseException("Unparseable date: \"12-11-1989\"", 10));
        ErrorLog errorLog3 = new PreparationErrorLog(preparation, "2014-13-31", new ParseException("Unparseable date: \"2014-13-31\"", 10));
        ErrorLog errorLog4 = new PreparationErrorLog(preparation, "2000-01-32", new ParseException("Unparseable date: \"2000-01-32\"", 10));

        trueErrorlogs.add(errorLog1);
        trueErrorlogs.add(errorLog2);
        trueErrorlogs.add(errorLog3);
        trueErrorlogs.add(errorLog4);
        ErrorRepository trueErrorRepository = new ErrorRepository(trueErrorlogs);

        pipeline.getRawData().show();

        Assert.assertEquals(trueErrorRepository, pipeline.getErrorRepository());
    }
}
