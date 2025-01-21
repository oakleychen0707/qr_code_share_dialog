# qr_code_share_dialog

[![Hits](https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2Foakleychen0707%2Fqr_code_share_dialog&count_bg=%23473DC8&title_bg=%23555555&icon=&icon_color=%23E7E7E7&title=hits&edge_flat=false)](https://hits.seeyoufarm.com)

## QR Code Share Dialog for Android

This Android project provides a customizable dialog that displays a share code and generates a corresponding QR Code. It includes features for copying the share code to the clipboard, downloading the QR Code, and closing the dialog.

### Features

- **Display Share Code**: Show a short share code in a text view.
- **QR Code Generation**: Dynamically generate a QR Code based on the share code.
- **Copy to Clipboard**: Allow users to easily copy the share code.
- **Download QR Code**: Let users save the generated QR Code to their device gallery.
- **Customizable UI**: Easily modify the dialog layout to fit your app's design.

### Screenshot

<p align="center">
  <img src="QRcode UI.png" width="300" />
</p>

### How It Works

The main function `showShareCodeDialog()` creates and displays a dialog that contains:
- A `TextView` to display the short share code.
- An `ImageView` to display the generated QR Code.
- Buttons to copy the share code and download the QR Code.

The QR Code is generated using the `generateQRCode()` method, and error handling is included for cases where QR Code generation fails.

---

## Code Snippet

### QR Code Share Dialog Implementation

```java
private void showShareCodeDialog() {
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View contentView = inflater.inflate(R.layout.dialog_ibon_share_code, null);
    ((TextView) contentView.findViewById(R.id.ibon_dialog_ibon_sharecode_txt)).setText(this.shortShareCode);
    Button copyBtn = contentView.findViewById(R.id.copy_button);
    ImageView closeBtn = contentView.findViewById(R.id.close_icon);
    ImageView qrCodeImg = contentView.findViewById(R.id.ibon_dialog_qrcode_image);
    Button downloadQRCodeBtn = contentView.findViewById(R.id.download_qrcode_button);
    Bitmap qrCodeBitmap = null;

    // Generate QR Code
    try {
        int size = 180;
        // shortShareCode can be any string value that you want to encode in the QR code.
        qrCodeBitmap = generateQRCode(this.shortShareCode, size, size);
        qrCodeImg.setImageBitmap(qrCodeBitmap);
    } catch (Exception e) {
        // Handle error
        e.printStackTrace();
        qrCodeImg.setVisibility(View.GONE);
        downloadQRCodeBtn.setVisibility(View.GONE);
        String failQRCode = context.getString(R.string.Word_QR_Code) + context.getString(R.string.fail_msg);
        Toast.makeText(context, failQRCode, Toast.LENGTH_LONG).show();
    }

    Dialog dialog = new Dialog(context);
    dialog.setContentView(contentView);

    copyBtn.setOnClickListener(v -> {
        ClipboardManager ClipMan = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipMan.setPrimaryClip(ClipData.newPlainText(context.getString(R.string.dialog_share_code), shortShareCode));
        dialog.dismiss();
    });

    Bitmap finalQrCodeBitmap = qrCodeBitmap;
    downloadQRCodeBtn.setOnClickListener(v -> {
        if (finalQrCodeBitmap != null) {
            saveQRCodeToGallery(context, finalQrCodeBitmap);
        } else {
            Toast.makeText(context, R.string.dialog_download_failed, Toast.LENGTH_SHORT).show();
        }
    });

    closeBtn.setOnClickListener(v -> dialog.dismiss());
    dialog.show();
}
```

### QR Code Generation Method

```java
private Bitmap generateQRCode(String content, int width, int height) throws WriterException {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                width,
                height
        );
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[y * width + x] = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }
```

### Saving QR Code to Gallery

```java
private void saveQRCodeToGallery(Context context, Bitmap bitmap) {
        String filename = context.getString(R.string.Word_QR_Code) + "_" + shortShareCode + ".png";
        OutputStream fos;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver resolver = context.getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/" + context.getString(R.string.Word_QR_Code));
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                if (imageUri != null) {
                    fos = resolver.openOutputStream(imageUri);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    if (fos != null) fos.close();
                    Toast.makeText(context, R.string.Download_Successful, Toast.LENGTH_SHORT).show();
                }
            } else {
                File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File subdirectory = new File(directory, context.getString(R.string.Word_QR_Code));
                if (!subdirectory.exists()) subdirectory.mkdirs();

                File imageFile = new File(subdirectory, filename);
                fos = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();

                MediaScannerConnection.scanFile(context, new String[]{imageFile.getAbsolutePath()}, null, (path, uri) -> {});
                Toast.makeText(context, R.string.Download_Successful, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, R.string.dialog_download_failed, Toast.LENGTH_SHORT).show();
        }
    }
```

---

## UI Layout Overview

This dialog layout is defined in `dialog_ibon_share_code.xml` and consists of the following components:

- **TextView** (`ibon_dialog_ibon_sharecode_txt`): Displays the short share code.
- **ImageView** (`ibon_dialog_qrcode_image`): Displays the generated QR code image.
- **Button** (`copy_button`): Allows users to copy the share code to clipboard.
- **Button** (`download_qrcode_button`): Allows users to download the QR code to their gallery.
- **ImageView** (`close_icon`): Allows users to close the dialog.

---

## Conclusion

Hope this helps you! 😊
