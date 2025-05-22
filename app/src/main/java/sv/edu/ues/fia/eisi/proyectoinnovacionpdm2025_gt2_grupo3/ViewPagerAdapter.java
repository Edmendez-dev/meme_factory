package sv.edu.ues.fia.eisi.proyectoinnovacionpdm2025_gt2_grupo3;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import sv.edu.ues.fia.eisi.proyectoinnovacionpdm2025_gt2_grupo3.tabCamera;
import sv.edu.ues.fia.eisi.proyectoinnovacionpdm2025_gt2_grupo3.tabGallery;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new tabGallery();
            case 1: return new tabCamera();
            default: return new tabGallery();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
