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
    private int id;

    public User()
    {
    }

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }
}
