package com.mass3d.render;

import java.util.LinkedHashMap;

/**
 * This class represents the relationship between a RenderingType and a RenderDevice. A RenderDevice
 * can have one RenderType.
 *
 * @param <T> an object wrapping an enum representing options for rendering a specific object
 */
public class DeviceRenderTypeMap<T>
    extends LinkedHashMap<RenderDevice, T> {

}
