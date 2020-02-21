#include <stdio.h>

int main()
{
	int t, n, ans = 0;
	int* p = NULL;
	scanf("%d", &n);
	for(int i = 0; i < n; i++)
		scanf("%d", &t), ans += t;
	printf("%d\n", ans);
	while(1);
	return 0;
}
