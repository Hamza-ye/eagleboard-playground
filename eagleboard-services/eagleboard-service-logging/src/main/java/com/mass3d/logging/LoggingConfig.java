package com.mass3d.logging;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.mass3d.common.DxfNamespaces;

@JacksonXmlRootElement( localName = "logging", namespace = DxfNamespaces.DXF_2_0 )
public class LoggingConfig
{
    private final LogLevel level;
    private final LogFormat format;
    private final boolean consoleEnabled;
    private final LogLevel consoleLevel;
    private final LogFormat consoleFormat;
    private final boolean fileEnabled;
    private final String fileName;
    private final LogLevel fileLevel;
    private final LogFormat fileFormat;
    private final boolean kafkaEnabled;
    private final LogLevel kafkaLevel;
    private final LogFormat kafkaFormat;
    private final String kafkaTopic;

    public LoggingConfig( LogLevel level, LogFormat format, boolean consoleEnabled, LogLevel consoleLevel, LogFormat consoleFormat,
        boolean fileEnabled, String fileName, LogLevel fileLevel, LogFormat fileFormat,
        boolean kafkaEnabled, LogLevel kafkaLevel, LogFormat kafkaFormat, String kafkaTopic )
    {
        this.level = level;
        this.format = format;
        this.consoleEnabled = consoleEnabled;
        this.consoleLevel = consoleLevel;
        this.consoleFormat = consoleFormat;
        this.fileEnabled = fileEnabled;
        this.fileName = fileName;
        this.fileLevel = fileLevel;
        this.fileFormat = fileFormat;
        this.kafkaEnabled = kafkaEnabled;
        this.kafkaLevel = kafkaLevel;
        this.kafkaFormat = kafkaFormat;
        this.kafkaTopic = kafkaTopic;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public LogLevel getLevel()
    {
        return level;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public LogFormat getFormat()
    {
        return format;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isConsoleEnabled()
    {
        return consoleEnabled;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public LogLevel getConsoleLevel()
    {
        return consoleLevel;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public LogFormat getConsoleFormat()
    {
        return consoleFormat != null ? consoleFormat : format;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isFileEnabled()
    {
        return fileEnabled;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getFileName()
    {
        return fileName;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public LogLevel getFileLevel()
    {
        return fileLevel;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public LogFormat getFileFormat()
    {
        return fileFormat;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isKafkaEnabled()
    {
        return kafkaEnabled;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public LogLevel getKafkaLevel()
    {
        return kafkaLevel;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public LogFormat getKafkaFormat()
    {
        return kafkaFormat != null ? kafkaFormat : format;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getKafkaTopic()
    {
        return kafkaTopic;
    }

    public boolean isTextFormat()
    {
        return LogFormat.TEXT == format;
    }

    public boolean isJsonFormat()
    {
        return LogFormat.JSON == format;
    }
}
