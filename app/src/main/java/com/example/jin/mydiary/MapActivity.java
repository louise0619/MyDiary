package com.example.jin.mydiary;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
	private static final int REQUEST_FINE_LOCATION = 1003;
	private GoogleMap mMap;
	private ArrayList<LatLng> positions = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		// 어디어디 위치를 저장함
		positions.add(new LatLng(37.450995, 127.127144));
		positions.add(new LatLng(37.452687, 127.129962));
		positions.add(new LatLng(37.446466, 127.128348));
		positions.add(new LatLng(37.444553, 127.131470));
		positions.add(new LatLng(37.513287, 127.100112));
		positions.add(new LatLng(37.535101, 127.094618));
		positions.add(new LatLng(37.516220, 127.130918));

		// activity_main의 map framgnet를 SupportMapFragment로 초기화
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		// mapFragment가 null이 아니면
		if (mapFragment != null) {
			// mapFragment에 MapActivity를 콜백으로 등록
			mapFragment.getMapAsync(this);
		} else {
			// mapFragment가 null이면 삭제
			finish();
		}
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		// 전역변수 mMap 갱신
		mMap = googleMap;
		// 지도 줌 버튼 활성화
		mMap.getUiSettings().setZoomControlsEnabled(true);

		// API level >= API 23(Marshmallow), must check Permission in Runtime
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
			} else {
				moveToCurrentPosition();
			}
		} else {
			moveToCurrentPosition();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == REQUEST_FINE_LOCATION && permissions.length > 0 && permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults.length > 0 && grantResults[0] == 0) {
			moveToCurrentPosition();
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	private void moveToCurrentPosition() {
		// 권한이 없으면 현재 위치를 가져오지 않음
		if (mMap == null || (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
			return;
		}
		// 현재 위치를 저장할 변수 선언
		Location location;

		// 시스템서비스에서 현재 위치를 가져옴
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		// locationManager가 null이 아니면
		if (locationManager != null) {
			// 위치제공자 목록을 가져옴
			List<String> providers = locationManager.getProviders(true);
			// 위치제공자 목록 순회
			for (int i = 0; i < providers.size(); ++i) {
				// i번째 위치제공자에게 마지막으로 알려진 위치를 location에 저장
				location = locationManager.getLastKnownLocation(providers.get(i));
				// location이 null이 아니면
				if (location != null) {
					// location에서 위도와 경도를 가져옴
					LatLng here = new LatLng(location.getLatitude(), location.getLongitude());
					// googleMap에 현재 위치(here)에 마커 추가
					mMap.addMarker(new MarkerOptions().position(here).title("현재 위치").snippet("현재 위치입니다"));
					// googleMap에 위에서 저장했던 위치에 마커 추가
					for (LatLng position : positions) {
						mMap.addMarker(new MarkerOptions().position(position));
					}
					// googleMap에서 현재 위치(here)로 이동
					mMap.moveCamera(CameraUpdateFactory.newLatLng(here));
					// 순회 종료
					break;
				}
			}
		}
	}
}
