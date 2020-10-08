package com.mass3d.node.serializers;

import com.fasterxml.jackson.dataformat.csv.CsvFactory;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.collect.Lists;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import com.mass3d.node.AbstractNodeSerializer;
import com.mass3d.node.Node;
import com.mass3d.node.types.CollectionNode;
import com.mass3d.node.types.ComplexNode;
import com.mass3d.node.types.RootNode;
import com.mass3d.node.types.SimpleNode;
import com.mass3d.system.util.DateUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.INTERFACES)
public class CsvNodeSerializer extends AbstractNodeSerializer {

  private static final String[] CONTENT_TYPES = {"application/csv", "text/csv"};

  private static final CsvMapper CSV_MAPPER = new CsvMapper();

  private static final CsvFactory CSV_FACTORY = CSV_MAPPER.getFactory();

  private CsvGenerator csvGenerator;

  @Override
  public List<String> contentTypes() {
    return Lists.newArrayList(CONTENT_TYPES);
  }

  @Override
  protected void startSerialize(RootNode rootNode, OutputStream outputStream) throws Exception {
    csvGenerator = CSV_FACTORY.createGenerator(outputStream);

    CsvSchema.Builder schemaBuilder = CsvSchema.builder()
        .setUseHeader(true);

    // build schema
    for (Node child : rootNode.getChildren()) {
      if (child.isCollection()) {
        if (!child.getChildren().isEmpty()) {
          Node node = child.getChildren().get(0);

          for (Node property : node.getChildren()) {
            if (property.isSimple()) {
              schemaBuilder.addColumn(property.getName());
            }
          }
        }
      }
    }

    csvGenerator.setSchema(schemaBuilder.build());
  }

  @Override
  protected void flushStream() throws Exception {
    csvGenerator.flush();
  }

  @Override
  protected void startWriteRootNode(RootNode rootNode) throws Exception {
    for (Node child : rootNode.getChildren()) {
      if (child.isCollection()) {
        for (Node node : child.getChildren()) {
          csvGenerator.writeStartObject();

          for (Node property : node.getChildren()) {
            if (property.isSimple()) {
              writeSimpleNode((SimpleNode) property);
            }
          }

          csvGenerator.writeEndObject();
        }
      }
    }
  }

  @Override
  protected void endWriteRootNode(RootNode rootNode) throws Exception {

  }

  @Override
  protected void startWriteSimpleNode(SimpleNode simpleNode) throws Exception {
    String value = String.format("%s", simpleNode.getValue());

    if (Date.class.isAssignableFrom(simpleNode.getValue().getClass())) {
      value = DateUtils.getIso8601NoTz((Date) simpleNode.getValue());
    }

    csvGenerator.writeObjectField(simpleNode.getName(), value);
  }

  @Override
  protected void endWriteSimpleNode(SimpleNode simpleNode) throws Exception {

  }

  @Override
  protected void startWriteComplexNode(ComplexNode complexNode) throws Exception {

  }

  @Override
  protected void endWriteComplexNode(ComplexNode complexNode) throws Exception {

  }

  @Override
  protected void startWriteCollectionNode(CollectionNode collectionNode) throws Exception {

  }

  @Override
  protected void endWriteCollectionNode(CollectionNode collectionNode) throws Exception {

  }

  @Override
  protected void dispatcher(Node node) throws Exception {

  }
}