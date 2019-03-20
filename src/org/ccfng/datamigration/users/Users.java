package org.ccfng.datamigration.users;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "ArrayOfUser")
@XmlAccessorType(XmlAccessType.FIELD)
public class Users {

    @XmlElement(name = "Users")
    private List<User> users = null;

    public Users() {
    }


    public List<User> getUsers() {
        return this.users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Users)) return false;
        final Users other = (Users) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$users = this.getUsers();
        final Object other$users = other.getUsers();
        if (this$users == null ? other$users != null : !this$users.equals(other$users))
            return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $users = this.getUsers();
        result = result * PRIME + ($users == null ? 43 : $users.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Users;
    }

    public String toString() {
        return "Users(users=" + this.getUsers() + ")";
    }
}
