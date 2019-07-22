package android.example.insuranceapp;
//https://riggaroo.co.za/app-invites-in-android/
public void sendShareEvent(String bookTitle, String bookImageUrl, String bookDeepLinkUrl) {
        try {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
        .setMessage(getString(R.string.invitation_message, bookTitle))
        .setDeepLink(Uri.parse(bookDeepLinkUrl))
        .setCustomImage(Uri.parse(bookImageUrl))
        .setCallToActionText(getString(R.string.invitation_cta))
        .build();
        startActivityForResult(intent, INVITE_REQUEST_CODE);
        } catch (ActivityNotFoundException ac) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.sharing_book_title, bookTitle));
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
        }
        }

protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INVITE_REQUEST_CODE) {
        if (resultCode == RESULT_OK) {
        String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
        Log.d(TAG, getString(R.string.sent_invitations_fmt, ids.length));
        } else {

        Log.d(TAG, "invite send failed or cancelled:" + requestCode + ",resultCode:" + resultCode );
        }
        }
        }

