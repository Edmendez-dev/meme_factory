package sv.edu.ues.fia.eisi.proyectoinnovacionpdm2025_gt2_grupo3;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPageStyleAdapter extends FragmentStateAdapter {
    public ViewPageStyleAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new tabText();
            case 1: return new tabStyle();
            default: return new tabText();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
