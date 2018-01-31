package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.cfg.BaseSettings;
import com.fasterxml.jackson.databind.deser.*;
import com.fasterxml.jackson.databind.introspect.BasicClassIntrospector;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import com.fasterxml.jackson.databind.jsontype.impl.StdSubtypeResolver;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.ser.*;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * Since {@link ObjectMapper} instances are immutable in  Jackson 3.x for full thread-safety,
 * we need means to construct configured instances. This is the shared base API for
 * builders for all types of mappers.
 *
 * @since 3.0
 */
public abstract class MapperBuilder<M extends ObjectMapper,
    B extends MapperBuilder<M,B>>
{
    /*
    /**********************************************************
    /* Basic settings
    /**********************************************************
     */

    protected BaseSettings _baseSettings;

    /*
    /**********************************************************
    /* Factories for framework itself, general
    /**********************************************************
     */

    /**
     * Underlying stream factory
     */
    protected final TokenStreamFactory _streamFactory;

    
    /**
     * Introspector used to figure out Bean properties needed for bean serialization
     * and deserialization. Overridable so that it is possible to change low-level
     * details of introspection, like adding new annotation types.
     */
    protected ClassIntrospector _classIntrospector;
    
    protected SubtypeResolver _subtypeResolver;

    /*
    /**********************************************************
    /* Factories for framework itself, serialization
    /**********************************************************
     */
    
    protected SerializerFactory _serializerFactory;

    /**
     * Prototype {@link SerializerProvider} to use for creating per-operation providers.
     */
    protected DefaultSerializerProvider _serializerProvider;

    /*
    /**********************************************************
    /* Factories for framework itself, deserialization
    /**********************************************************
     */

    protected DeserializerFactory _deserializerFactory;
    
    /**
     * Prototype (about same as factory) to use for creating per-operation contexts.
     */
    protected DefaultDeserializationContext _deserializationContext;

    /*
    /**********************************************************
    /* Configuration settings, shared
    /**********************************************************
     */
    
    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */

    protected MapperBuilder(TokenStreamFactory streamFactory)
    {
        _baseSettings = BaseSettings.std();
        _streamFactory = streamFactory;

        _classIntrospector = null;
        _subtypeResolver = null;

        _serializerFactory = BeanSerializerFactory.instance;
        _serializerProvider = null;

        _deserializerFactory = BeanDeserializerFactory.instance;
        _deserializationContext = null;

        //        _mapperFeatures = MapperFeature;
    }

    protected MapperBuilder(MapperBuilder<?,?> base)
    {
        _baseSettings = base._baseSettings;
        _streamFactory = base._streamFactory;

        _classIntrospector = base._classIntrospector;
        _subtypeResolver = base._subtypeResolver;

        _serializerFactory = base._serializerFactory;
        _serializerProvider = base._serializerProvider;

        _deserializerFactory = base._deserializerFactory;
        _deserializationContext = base._deserializationContext;
    }

    /**
     * Method to call to create an initialize actual mapper instance
     */
    public abstract M build();

    /*
    /**********************************************************
    /* Accessors, general
    /**********************************************************
     */

    public BaseSettings baseSettings() {
        return _baseSettings;
    }

    public TokenStreamFactory streamFactory() {
        return _streamFactory;
    }

    public TypeFactory typeFactory() {
        return _baseSettings.getTypeFactory();
    }

    public ClassIntrospector classIntrospector() {
        return (_classIntrospector != null) ? _classIntrospector : defaultClassIntrospector();
    }

    /**
     * Overridable method for changing default {@link SubtypeResolver} instance to use
     */
    protected ClassIntrospector defaultClassIntrospector() {
        return new BasicClassIntrospector();
    }

    public SubtypeResolver subtypeResolver() {
        return (_subtypeResolver != null) ? _subtypeResolver : defaultSubtypeResolver();
    }

    /**
     * Overridable method for changing default {@link SubtypeResolver} prototype
     * to use.
     */
    protected SubtypeResolver defaultSubtypeResolver() {
        return new StdSubtypeResolver();
    }

    /*
    /**********************************************************
    /* Accessors, serialization
    /**********************************************************
     */

    public SerializerFactory serializerFactory() {
        return _serializerFactory;
    }

    public DefaultSerializerProvider serializerProvider() {
        return (_serializerProvider != null) ? _serializerProvider : defaultSerializerProvider();
    }

    /**
     * Overridable method for changing default {@link SerializerProvider} prototype
     * to use.
     */
    protected DefaultSerializerProvider defaultSerializerProvider() {
        return new DefaultSerializerProvider.Impl(_streamFactory);
    }

    /*
    /**********************************************************
    /* Accessors, deserialization
    /**********************************************************
     */

    public DeserializerFactory deserializerFactory() {
        return _deserializerFactory;
    }

    protected DefaultDeserializationContext deserializationContext() {
        return (_deserializationContext != null) ? _deserializationContext
                : defaultDeserializationContext();
    }

    /**
     * Overridable method for changing default {@link SerializerProvider} prototype
     * to use.
     */
    protected DefaultDeserializationContext defaultDeserializationContext() {
        return new DefaultDeserializationContext.Impl(deserializerFactory(),
                _streamFactory);
    }

    /*
    /**********************************************************
    /* Changing factories, general
    /**********************************************************
     */

    public B typeFactory(TypeFactory f) {
        _baseSettings = _baseSettings.with(f);
        return _this();
    }

    protected B nodeFactory(JsonNodeFactory f) {
        _baseSettings = _baseSettings.with(f);
        return _this();
    }

    public B subtypeResolver(SubtypeResolver r) {
        _subtypeResolver = r;
        return _this();
    }

    public B classIntrospector(ClassIntrospector ci) {
        _classIntrospector = ci;
        return _this();
    }

    /*
    /**********************************************************
    /* Changing factories, serialization
    /**********************************************************
     */
    
    public B serializerFactory(SerializerFactory f) {
        _serializerFactory = f;
        return _this();
    }

    public B serializerProvider(DefaultSerializerProvider prov) {
        _serializerProvider = prov;
        return _this();
    }

    /*
    /**********************************************************
    /* Changing factories, deserialization
    /**********************************************************
     */

    public B deserializerFactory(DeserializerFactory f) {
        _deserializerFactory = f;
        return _this();
    }

    protected B deserializationContext(DefaultDeserializationContext ctxt) {
        _deserializationContext = ctxt;
        return _this();
    }

    /*
    /**********************************************************
    /* Other helper methods
    /**********************************************************
     */
    
    // silly convenience cast method we need
    @SuppressWarnings("unchecked")
    protected final B _this() { return (B) this; }
}