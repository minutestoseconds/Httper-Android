package org.mushare.httper;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.mushare.httper.utils.StringUtils.SplitString;

/**
 * Created by dklap on 5/4/2017.
 */

public class ResponseRawFragment extends AbstractSaveFileFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_response_listview, container, false);
        ListView listView = view.findViewById(R.id.listView);
        ResponseActivity activity = ((ResponseActivity) getActivity());
        if (activity != null) {
            listView.setAdapter(new ArrayAdapter<>(getContext(), R.layout.list_response_textview,
                    SplitString(activity.responseBody)));
            registerForContextMenu(listView);
        }
        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo
            menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, R.id.context_menu_copy, 0, R.string.context_menu_copy);
        menu.add(Menu.NONE, R.id.context_menu_save, 1, R.string.context_menu_save);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.context_menu_copy) {
            try {
                ClipboardManager cm = (ClipboardManager) getContext().getSystemService
                        (Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("response_body", ((ResponseActivity) getActivity()).responseBody));
                Toast.makeText(getContext(), R.string.toast_copy, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getContext(), R.string.copy_error, Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (item.getItemId() == R.id.context_menu_save) {
            preSaveFile();
            return true;
        } else return super.onContextItemSelected(item);
    }

    @Override
    public void saveFile(OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[8 * 1024];
        int bytesRead;
        FileInputStream in = new FileInputStream(((ResponseActivity) getActivity()).cacheFile);
        while ((bytesRead = in.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        in.close();
        outputStream.flush();
    }

    @Override
    public String defaultFileName() {
        Uri uri = Uri.parse(((ResponseActivity) getActivity()).url);
        String fileName = uri.getLastPathSegment();
        if (fileName == null || fileName.isEmpty()) fileName = uri.getHost();
        return (fileName == null || fileName.isEmpty() ? String.valueOf(System.currentTimeMillis
                ()) : fileName);
    }
}
