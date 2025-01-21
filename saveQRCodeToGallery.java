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
