import java.util.UUID;
import java.util.Date;

class ShareInvitation {

    private final UUID           id;
    private final TaskListImpl   taskList;
    private final User           invitedBy;
    private final User           invitedUser;
    private final SharePermission permission;
    private String               status;   // "PENDING", "ACCEPTED", "DECLINED"
    private final Date           sentAt;

    public ShareInvitation(TaskListImpl list, User from, User to, SharePermission perm) {
        if (list == null || from == null || to == null || perm == null)
            throw new IllegalArgumentException("Parametres d'invitation invalides.");
        this.id          = UUID.randomUUID();
        this.taskList    = list;
        this.invitedBy   = from;
        this.invitedUser = to;
        this.permission  = perm;
        this.status      = "PENDING";
        this.sentAt      = new Date();
    }

    public void accept() {
        if (!"PENDING".equals(status)) throw new IllegalStateException("Invitation deja traitee.");
        this.status = "ACCEPTED";
        taskList.setShared(true);
    }

    public void decline() {
        if (!"PENDING".equals(status)) throw new IllegalStateException("Invitation deja traitee.");
        this.status = "DECLINED";
    }

    public UUID            getId()          { return id; }
    public String          getStatus()      { return status; }
    public SharePermission getPermission()  { return permission; }
    public User            getInvitedBy()   { return invitedBy; }
    public User            getInvitedUser() { return invitedUser; }
    public TaskListImpl    getTaskList()    { return taskList; }
    public Date            getSentAt()      { return sentAt; }
}
