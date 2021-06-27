package com.bigjeon.grumbling;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;

import com.bigjeon.grumbling.adapter.Fragment_Swipe_Adapter;
import com.bigjeon.grumbling.fragments.Post_View_Fragment;
import com.bigjeon.grumbling.fragments.Setting_Fragment;
import com.bigjeon.grumbling.fragments.TimeLine_Fragment;
import com.example.grumbling.App_Main_Binding;
import com.example.grumbling.R;
import com.squareup.picasso.Picasso;

public class App_Main_Activity extends AppCompatActivity implements View.OnCreateContextMenuListener{
    public static Context mcontext;
    App_Main_Binding binding;
    public String My_Uid;
    public String My_Img;
    public String My_Name;
    public String My_Email;
    private String Show_Grade = "모든 게시글";
    private Post_View_Fragment frag = new Post_View_Fragment();
    private FragmentStateAdapter ViewPager_Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_main);
        binding.setAppMainActivity(this);

        mcontext = this;
        Set_My_Data();

        ViewPager_Adapter = new Fragment_Swipe_Adapter(this);
        binding.AppMainViewPager2.setAdapter(ViewPager_Adapter);
        binding.AppMainViewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        binding.AppMainViewPager2.setCurrentItem(1, false);
        binding.AppMainViewPager2.setOffscreenPageLimit(1);

        binding.AppMainViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0){
                    binding.AppMainViewPager2.setCurrentItem(position);
                }
            }
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Button_Background_Change(position);
            }
        });

        binding.AppMainUserImgCircleImv.setOnClickListener( v -> Go_Profile_Set_Act());

        binding.AppMainPostBtn.setOnCreateContextMenuListener(this);
    }

    private void Go_Profile_Set_Act() {
        Intent Set_Profile_Intent = new Intent(this, Set_User_Profile_Activity.class);
        Set_Profile_Intent.putExtra("UID", My_Uid);
        Set_Profile_Intent.putExtra("CODE", "CHANGE_SET");
        Set_Profile_Intent.putExtra("EMAIL", My_Email);
        startActivity(Set_Profile_Intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuItem All_Post = menu.add(Menu.NONE, R.menu.post_view_grade_menu, 1, "모든 게시글");
        MenuItem My_Post = menu.add(Menu.NONE, R.menu.post_view_grade_menu, 2, "나의 게시글");
        MenuItem Favorite_Post = menu.add(Menu.NONE, R.menu.post_view_grade_menu, 3, "좋아요 게시글");
        All_Post.setOnMenuItemClickListener(OnMenuClicked);
        My_Post.setOnMenuItemClickListener(OnMenuClicked);
        Favorite_Post.setOnMenuItemClickListener(OnMenuClicked);
    }

    private final MenuItem.OnMenuItemClickListener OnMenuClicked = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getOrder()){
                case 1 :
                    Show_Grade = "모든 게시글";
                    binding.AppMainPostBtn.setText(Show_Grade);
                    //((Post_View_Fragment)getSupportFragmentManager().findFragmentById(R.id.App_Main_Fragment_FrameLayout)).Get_Post(Show_Grade);
                    return true;
                case 2 :
                    Show_Grade = "나의 게시글";
                    binding.AppMainPostBtn.setText(Show_Grade);
                    //((Post_View_Fragment)findFragmentByPosition(1)).Get_Post(Show_Grade);
                    return true;
                case 3 :
                    Show_Grade = "좋아요 게시글";
                    binding.AppMainPostBtn.setText(Show_Grade);
                    //((Post_View_Fragment)findFragmentByPosition(1)).Get_Post(Show_Grade);
                    return true;
            }
            return false;
        }
    };
    //미구현(널에러)
    private Fragment findFragmentByPosition(int position){
    return getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.App_Main_ViewPager2 + ":" + ViewPager_Adapter.getItemId(position));
    }
    public String Set_Grade(){
        return Show_Grade;
    }

    private void Set_My_Data(){
        SharedPreferences My_Data = getSharedPreferences("My_Data", MODE_PRIVATE);
        My_Uid = My_Data.getString("UID", null);
        My_Name = My_Data.getString("NAME", null);
        My_Img = My_Data.getString("IMG", null);
        My_Email = My_Data.getString("EMAIL", null);
        Picasso.get().load(My_Img).into(binding.AppMainUserImgCircleImv);
    }

    private void Button_Background_Change(int position){
        switch (position){
            case 0 :
                binding.AppMainTimeLineBtn.setTextColor(getColor(R.color.purple_200));
                binding.AppMainPostBtn.setTextColor(getColor(R.color.Gray));
                binding.AppMainSettingBtn.setTextColor(getColor(R.color.Gray));
                break;
            case 1 :
                binding.AppMainTimeLineBtn.setTextColor(getColor(R.color.Gray));
                binding.AppMainPostBtn.setTextColor(getColor(R.color.purple_200));
                binding.AppMainSettingBtn.setTextColor(getColor(R.color.Gray));
                break;
            case 2 :
                binding.AppMainTimeLineBtn.setTextColor(getColor(R.color.Gray));
                binding.AppMainPostBtn.setTextColor(getColor(R.color.Gray));
                binding.AppMainSettingBtn.setTextColor(getColor(R.color.purple_200));
                break;
        }
    }
}