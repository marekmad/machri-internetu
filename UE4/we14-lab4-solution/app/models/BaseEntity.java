package models;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Base entity for all JPA classes
 */
public class BaseEntity {

	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    public Long getId() {
        return id;
    }

}
