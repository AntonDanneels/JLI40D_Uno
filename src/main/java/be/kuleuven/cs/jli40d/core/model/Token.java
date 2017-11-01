package be.kuleuven.cs.jli40d.core.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Pieter
 * @version 1.0
 */
@Entity
public class Token implements Serializable
{
    @Id
    private long id;

    private String token;

    private Date expiryDate;

    public Token()
    {
    }

    public Token( String token, Date expiryDate )
    {
        this.token = token;
        this.expiryDate = expiryDate;
    }

    public long getId()
    {
        return id;
    }

    public void setId( long id )
    {
        this.id = id;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken( String token )
    {
        this.token = token;
    }

    public Date getExpiryDate()
    {
        return expiryDate;
    }

    public void setExpiryDate( Date expiryDate )
    {
        this.expiryDate = expiryDate;
    }
}
