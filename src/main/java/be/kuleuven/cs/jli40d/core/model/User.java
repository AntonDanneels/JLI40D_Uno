package be.kuleuven.cs.jli40d.core.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author Pieter
 * @version 1.0
 */
@Entity
public class User implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String username;

    private long score;

    private String password;

    public User()
    {
    }

    public User( String username, long score, String password )
    {
        this.username = username;
        this.score = score;
        this.password = password;
    }

    public long getId()
    {
        return id;
    }

    public void setId( long id )
    {
        this.id = id;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }

    public long getScore()
    {
        return score;
    }

    public void setScore( long score )
    {
        this.score = score;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword( String password )
    {
        this.password = password;
    }
}
