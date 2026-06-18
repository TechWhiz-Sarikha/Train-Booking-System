package model;

public class Customer {
    private int customerId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String password;
    private boolean active;

    public Customer() {}

    public Customer(String name, String email, String phone, String address, String password) {
        this.name     = name;
        this.email    = email;
        this.phone    = phone;
        this.address  = address;
        this.password = password;
        this.active   = true;
    }

    public int getCustomerId()          { return customerId; }
    public void setCustomerId(int id)   { this.customerId = id; }

    public String getName()             { return name; }
    public void setName(String n)       { this.name = n; }

    public String getEmail()            { return email; }
    public void setEmail(String e)      { this.email = e; }

    public String getPhone()            { return phone; }
    public void setPhone(String p)      { this.phone = p; }

    public String getAddress()          { return address; }
    public void setAddress(String a)    { this.address = a; }

    public String getPassword()         { return password; }
    public void setPassword(String p)   { this.password = p; }

    public boolean isActive()           { return active; }
    public void setActive(boolean a)    { this.active = a; }

    @Override
    public String toString() {
        return String.format(
            "ID: %d | Name: %-20s | Email: %-30s | Phone: %s | Status: %s",
            customerId, name, email, phone, active ? "Active" : "Inactive"
        );
    }
}
