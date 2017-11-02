package be.kuleuven.cs.jli40d.core.model;

import javax.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String token;

    @Temporal( TemporalType.DATE )
    private Date expiryDate;

    @ManyToOne
    private User user;

    public Token()
    {
    }

    public Token( String token, Date expiryDate, User user )
    {
        this.token = token;
        this.expiryDate = expiryDate;
        this.user = user;
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

    public User getUser()
    {
        return user;
    }

    public void setUser( User user )
    {
        this.user = user;
    }
}
