/*****************************************************************************
 * Copyright (C) Compart AG, 2016 - Compart confidential
 *
 *****************************************************************************/

package com.compart.tec.orientdb.object;

import javax.persistence.Basic;
import javax.persistence.Id;
import javax.persistence.Version;

import com.orientechnologies.orient.core.annotation.OId;
import com.orientechnologies.orient.core.annotation.OVersion;
import org.apache.commons.lang.StringUtils;

/**
 * Base class for entities.
 */
public abstract class BaseEntity
{

    @Id
    @OId
    public String id;

    @Basic
    public String name;

    @Version
    @OVersion
    public String version;

    public String getId()
    {
        return this.id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getVersion()
    {
        return this.version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        else if (!(obj instanceof BaseEntity))
        {
            return false;
        }

        BaseEntity that = (BaseEntity) obj;
        if (this.id == null && that.id == null)
        {
            return false;
        }

        return StringUtils.equals(id, that.id);
    }

    @Override
    public int hashCode()
    {
        if (id == null)
        {
            return super.hashCode();
        }
        return id.hashCode();
    }
}
