private void showShareCodeDialog() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.dialog_ibon_share_code, null);
        ((TextView) contentView.findViewById(R.id.ibon_dialog_ibon_sharecode_txt)).setText(this.shortShareCode);
        Button copyBtn = contentView.findViewById(R.id.copy_button);
        ImageView closeBtn = contentView.findViewById(R.id.close_icon);
        ImageView qrCodeImg = contentView.findViewById(R.id.ibon_dialog_qrcode_image);
        Button downloadQRCodeBtn = contentView.findViewById(R.id.download_qrcode_button);
        Bitmap qrCodeBitmap = null;

        // 產生 QR Code
        try {
            int size = 180;
            qrCodeBitmap = generateQRCode(this.shortShareCode, size, size);
            qrCodeImg.setImageBitmap(qrCodeBitmap);
        } catch (Exception e) {
            // 無法產生 QR Code
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
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                Toast.makeText(context.getApplicationContext(), R.string.msg_selected_content, Toast.LENGTH_LONG).show();
            }
            dialog.dismiss();
        });

        Bitmap finalQrCodeBitmap = qrCodeBitmap;
        downloadQRCodeBtn.setOnClickListener(v -> {
            if (finalQrCodeBitmap != null) {
                saveQRCodeToGallery(context, finalQrCodeBitmap);
            } else {
                // QR Code 不存在無法下載
                Toast.makeText(context, R.string.dialog_download_failed, Toast.LENGTH_SHORT).show();
            }
        });

        closeBtn.setOnClickListener(v -> dialog.dismiss());
        dialog.show();

    }
