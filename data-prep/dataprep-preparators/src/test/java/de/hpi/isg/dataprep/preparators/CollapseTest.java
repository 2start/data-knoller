package de.hpi.isg.dataprep.preparators;

import de.hpi.isg.dataprep.exceptions.MetadataNotFoundException;
import de.hpi.isg.dataprep.model.repository.ErrorRepository;
import de.hpi.isg.dataprep.model.target.system.Preparation;
import de.hpi.isg.dataprep.model.target.errorlog.ErrorLog;
import de.hpi.isg.dataprep.model.target.errorlog.PipelineErrorLog;
import de.hpi.isg.dataprep.model.target.preparator.Preparator;
import de.hpi.isg.dataprep.util.DataType;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lan Jiang
 * @since 2018/8/28
 */
public class CollapseTest extends PreparatorTest {

    @Test
    public void testCollapse() throws Exception {
        Preparator preparator = new Collapse("identifier");

        Preparation preparation = new Preparation(preparator);
        pipeline.addPreparation(preparation);
        pipeline.executePipeline();

        List<ErrorLog> trueErrorlogs = new ArrayList<>();

        ErrorLog pipelineError1 = new PipelineErrorLog(pipeline,
                new MetadataNotFoundException(String.format("The metadata %s not found in the repository.", "PropertyDataType{" +
                        "propertyName='" + "identifier" + '\'' +
                        ", propertyDataType=" + DataType.PropertyType.STRING.toString() +
                        '}')));
        trueErrorlogs.add(pipelineError1);
        ErrorRepository trueErrorRepository = new ErrorRepository(trueErrorlogs);

        pipeline.getRawData().show();
        pipeline.getRawData().printSchema();

        Assert.assertEquals(trueErrorRepository, pipeline.getErrorRepository());
    }
}
