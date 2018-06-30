package com.example.kitstasher.other;

public interface OnPagerItemInteractionListener {
    void onPagerItemDelete(final long itemId, final int currentPosition, final String uri,
                           final String onlineId);
}
