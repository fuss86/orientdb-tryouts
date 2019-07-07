
package com.compart.tec.orientdb.object;

import com.compart.tec.orientdb.unit.AbstractOrientDBObjectITest;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author diegomtassis <a href="mailto:dta@compart.com">Diego Martin Tassis</a>
 * @author Przemyslaw Fusik
 */
public class PropagateRemoveOnSetFieldIT
        extends AbstractOrientDBObjectITest
{

    public PropagateRemoveOnSetFieldIT()
    {
        super(PropagateRemoveOnSetFieldIT.class.getSimpleName());
    }

    /**
     * It works
     */
    @Test
    public void testImmediateSetObjectRemoval()
    {

        // setup: infrastructure
        registerEntities();

        // setup: data
        Man ayrton = new Man();
        Man alain = new Man();
        ayrton.addFriend(alain);
        Man savedAyrton = this.getDatabase().save(ayrton);
        String ayrtonId = savedAyrton.getId();

        // exercise
        Person retrievedAyrton = this.getDatabase().load(new ORecordId(ayrtonId));
        Assert.assertEquals(1, retrievedAyrton.getFriends().size());
        retrievedAyrton = ((OObjectDatabaseTx) getDatabase()).detachAll(retrievedAyrton, true);
        Person retrievedAlain = retrievedAyrton.getFriends().iterator().next();
        retrievedAyrton.getFriends().remove(retrievedAlain);
        Assert.assertEquals(0, retrievedAyrton.getFriends().size());
        this.getDatabase().save(retrievedAyrton);

        // verify
        retrievedAyrton = this.getDatabase().load(new ORecordId(ayrtonId));
        retrievedAyrton = ((OObjectDatabaseTx) getDatabase()).detachAll(retrievedAyrton, true);
        Assert.assertEquals(0, retrievedAyrton.getFriends().size());
    }

    /**
     * It fails
     */
    @Test
    public void testCascadeSetObjectRemoval()
    {

        // setup: infrastructure
        registerEntities();

        // setup: data
        Man ayrton = new Man();
        Man alain = new Man();
        Man peter = new Man();
        ayrton.addFriend(alain);
        alain.addFriend(peter);
        Man savedAyrton = this.getDatabase().save(ayrton);
        String ayrtonId = savedAyrton.getId();

        // exercise
        Person retrievedAyrton = this.getDatabase().load(new ORecordId(ayrtonId));
        Assert.assertEquals(1, retrievedAyrton.getFriends().iterator().next().getFriends().size());
        retrievedAyrton = ((OObjectDatabaseTx) getDatabase()).detachAll(retrievedAyrton, true);
        Person retrievedAlain = retrievedAyrton.getFriends().iterator().next();
        Assert.assertEquals(1, retrievedAlain.getFriends().size());
        retrievedAlain.getFriends().remove(peter);
        Assert.assertEquals(0, retrievedAlain.getFriends().size());
        this.getDatabase().save(retrievedAyrton);

        // verify
        retrievedAyrton = this.getDatabase().load(new ORecordId(ayrtonId));
        retrievedAyrton = ((OObjectDatabaseTx) getDatabase()).detachAll(retrievedAyrton, true);
        Assert.assertEquals(0, retrievedAyrton.getFriends().iterator().next().getFriends().size());
    }

    /**
     * It throws exception in 3.0.21
     * It fails in 3.0.0
     */
    @Test
    public void testCascadeSetObjectUpdate()
    {

        // setup: infrastructure
        registerEntities();

        // setup: data
        Man ayrton = new Man();
        Man alain = new Man();
        Man peter = new Man();
        ayrton.addFriend(alain);
        alain.addFriend(peter);
        peter.setFavoriteFood("pizza");
        Man savedAyrton = this.getDatabase().save(ayrton);
        String ayrtonId = savedAyrton.getId();

        // exercise
        Person retrievedAyrton = this.getDatabase().load(new ORecordId(ayrtonId));
        Assert.assertEquals(1, retrievedAyrton.getFriends().iterator().next().getFriends().size());
        retrievedAyrton = ((OObjectDatabaseTx) getDatabase()).detachAll(retrievedAyrton, true);
        Person retrievedAlain = retrievedAyrton.getFriends().iterator().next();
        Person retrievedPeter = retrievedAlain.getFriends().iterator().next();
        Assert.assertEquals("pizza", retrievedPeter.getFavoriteFood());
        retrievedPeter.setFavoriteFood("burger");
        Assert.assertEquals("burger", retrievedPeter.getFavoriteFood());
        this.getDatabase().save(retrievedAyrton);

        // verify
        retrievedAyrton = this.getDatabase().load(new ORecordId(ayrtonId));
        retrievedAyrton = ((OObjectDatabaseTx) getDatabase()).detachAll(retrievedAyrton, true);
        retrievedAlain = retrievedAyrton.getFriends().iterator().next();
        retrievedPeter = retrievedAlain.getFriends().iterator().next();
        Assert.assertEquals("burger", retrievedPeter.getFavoriteFood());
    }

    private void registerEntities()
    {
        getDatabase().getEntityManager().registerEntityClasses(Man.class.getPackage().getName());
    }
}
