package de.hpi.isg.dataprep.implementation

import de.hpi.isg.dataprep.exceptions.PreparationHasErrorException
import de.hpi.isg.dataprep.model.error.{PreparationError, PropertyError}
import de.hpi.isg.dataprep.model.target.preparator.{Preparator, PreparatorImpl}
import de.hpi.isg.dataprep.preparators.MoveProperty
import de.hpi.isg.dataprep.{Consequences, SchemaUtils}
import org.apache.spark.sql.{Dataset, Row}
import org.apache.spark.util.CollectionAccumulator

/**
  *
  * @author Lan Jiang
  * @since 2018/8/21
  */
class DefaultMovePropertyImpl extends PreparatorImpl {

    @throws(classOf[Exception])
    override protected def executePreparator(preparator: Preparator, dataFrame: Dataset[Row]): Consequences = {
        val preparator_ = this.getPreparatorInstance(preparator, classOf[MoveProperty])
        val errorAccumulator = this.createErrorAccumulator(dataFrame)
        executeLogic(preparator_, dataFrame, errorAccumulator)
    }

    protected def executeLogic(preparator: MoveProperty, dataFrame: Dataset[Row], errorAccumulator: CollectionAccumulator[PreparationError]): Consequences = {
        val propertyName = preparator.propertyName
        val newPosition = preparator.newPosition

        val schema = dataFrame.columns

        if (!SchemaUtils.positionValidation(newPosition, schema)) {
            errorAccumulator.add(new PropertyError(propertyName,
                new PreparationHasErrorException(String.format("New position %d in the schema is out of bound.", newPosition : Integer))))
        }

        val currentPosition = dataFrame.schema.fieldIndex(propertyName)

        var fore = schema.take(currentPosition)
        var back = schema.drop(currentPosition+1)
        val intermediate = (fore ++ back).clone()

        fore = intermediate.take(newPosition)
        back = intermediate.drop(newPosition)
        val newSchema = (fore :+ schema(currentPosition)) ++: back
        val columns = newSchema.map(col => dataFrame.col(col))

        val resultDataFrame = dataFrame.select(columns: _*)

        new Consequences(resultDataFrame, errorAccumulator)
    }
}
