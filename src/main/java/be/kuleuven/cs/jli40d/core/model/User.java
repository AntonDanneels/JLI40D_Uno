package be.kuleuven.cs.jli40d.core.model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Pieter
 * @version 1.0
 */
@Entity
public class User
{
    @Id
    private long id;

    private long score;

    private String password;

    public User()
    {
    }

    public User( long score, String password )
    {
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
