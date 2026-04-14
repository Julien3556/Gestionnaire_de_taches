import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

class SharingService {

    // Map: list UUID -> (user UUID -> ShareInvitation)
    private final Map<UUID, Map<UUID, ShareInvitation>> grants = new HashMap<>();

    public ShareInvitation shareList(TaskListImpl list, User from, User to, SharePermission perm) {
        ShareInvitation inv = new ShareInvitation(list, from, to, perm);
        grants.computeIfAbsent(list.getOwner().getId(), k -> new HashMap<>()).put(to.getId(), inv);
        return inv;
    }

    public void revokeAccess(TaskListImpl list, User user) {
        Map<UUID, ShareInvitation> m = grants.get(list.getOwner().getId());
        if (m != null) m.remove(user.getId());
    }

    public boolean canAccess(User user, Task task) {
        if (task.getOwner().getId().equals(user.getId())) return true;
        if (!task.isPrivate()) return true;
        Map<UUID, ShareInvitation> m = grants.get(task.getOwner().getId());
        if (m == null) return false;
        ShareInvitation inv = m.get(user.getId());
        return inv != null && "ACCEPTED".equals(inv.getStatus());
    }

    public List<TaskListImpl> getSharedWithMe(User user) {
        List<TaskListImpl> result = new ArrayList<>();
        for (Map<UUID, ShareInvitation> m : grants.values()) {
            ShareInvitation inv = m.get(user.getId());
            if (inv != null && "ACCEPTED".equals(inv.getStatus())) result.add(inv.getTaskList());
        }
        return result;
    }

    public List<User> getCollaborators(TaskListImpl list) {
        Map<UUID, ShareInvitation> m = grants.get(list.getOwner().getId());
        if (m == null) return Collections.emptyList();
        return m.values().stream()
            .filter(inv -> "ACCEPTED".equals(inv.getStatus()))
            .map(ShareInvitation::getInvitedUser)
            .collect(Collectors.toList());
    }
}
